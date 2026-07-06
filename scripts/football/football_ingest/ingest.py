"""Pipeline de ingestión football-data.org -> PostgreSQL."""

from __future__ import annotations

import logging
from typing import Any

import requests
from psycopg2.extensions import connection as PgConnection

from .config import Settings
from .db import FootballDb
from .football_data_client import FootballDataClient
from .mappers import (
    extract_standings_summary,
    map_competition,
    map_match,
    map_team,
)

log = logging.getLogger(__name__)


class FootballIngestService:
    def __init__(self, settings: Settings) -> None:
        self._settings = settings
        self._api = FootballDataClient(settings)
        self._db = FootballDb(settings.database_url)

    def run(self) -> dict[str, int]:
        """Ingesta una competición/temporada (modo simple)."""
        return self.ingest_competition_season(
            self._settings.competition_code,
            self._settings.season_year,
        )

    def run_all(self) -> dict[str, Any]:
        """Ingesta todas las competiciones configuradas, varias temporadas."""
        totals: dict[str, int] = {
            "competitions": 0,
            "seasons": 0,
            "teams": 0,
            "matches": 0,
            "skipped_seasons": 0,
            "errors": 0,
        }
        skipped: list[str] = []

        for code, label in self._settings.competitions.items():
            log.info("═══ %s (%s) ═══", label, code)
            try:
                competition_api = self._api.get_competition(code)
            except requests.HTTPError as exc:
                if exc.response is not None and exc.response.status_code == 403:
                    log.warning("Competición %s no disponible en tu plan API — omitida", code)
                    skipped.append(f"{code} (competición restringida)")
                    totals["errors"] += 1
                    continue
                raise

            season_years = self._season_years_for_competition(competition_api)
            log.info("Temporadas a intentar: %s", season_years)

            with self._db.connect() as conn:
                competition_id = self._upsert_competition(conn, code, competition_api)
                totals["competitions"] += 1

                for year in season_years:
                    try:
                        stats = self._ingest_season(conn, code, competition_id, year)
                        totals["seasons"] += stats["seasons"]
                        totals["teams"] += stats["teams"]
                        totals["matches"] += stats["matches"]
                    except requests.HTTPError as exc:
                        if exc.response is not None and exc.response.status_code == 403:
                            log.warning(
                                "Temporada %s/%s restringida en plan free — omitida",
                                code,
                                year,
                            )
                            skipped.append(f"{code}/{year}")
                            totals["skipped_seasons"] += 1
                            continue
                        raise

        result = {**totals, "skipped": skipped}
        log.info("Ingestión masiva completada: %s", result)
        return result

    def ingest_competition_season(self, code: str, season_year: str) -> dict[str, int]:
        log.info("Ingestión: %s temporada %s", code, season_year)
        competition_api = self._api.get_competition(code)
        stats = {"competitions": 0, "seasons": 0, "teams": 0, "matches": 0}

        with self._db.connect() as conn:
            competition_id = self._upsert_competition(conn, code, competition_api)
            stats["competitions"] = 1
            season_stats = self._ingest_season(conn, code, competition_id, season_year)
            stats.update(season_stats)

        log.info("Ingestión completada: %s", stats)
        return stats

    def _season_years_for_competition(self, competition_api: dict[str, Any]) -> list[str]:
        """Temporadas recientes según metadata de la API, filtradas por season_from/season_to."""
        seasons = competition_api.get("seasons") or []
        years: list[int] = []
        for s in seasons:
            start = (s.get("startDate") or "")[:4]
            if start.isdigit():
                years.append(int(start))

        if not years:
            current = (competition_api.get("currentSeason") or {}).get("startDate", "")[:4]
            if current.isdigit():
                years = [int(current)]

        lo = self._settings.season_from
        hi = self._settings.season_to or max(years, default=lo)
        selected = sorted({y for y in years if lo <= y <= hi})
        return [str(y) for y in selected]

    def _ingest_season(
        self,
        conn: PgConnection,
        code: str,
        competition_id: int,
        season_year: str,
    ) -> dict[str, int]:
        log.info("  Temporada %s", season_year)
        stats = {"seasons": 0, "teams": 0, "matches": 0}

        teams_api = self._api.get_teams(code, season_year)
        season_id = self._upsert_season(conn, code, competition_id, season_year)
        stats["seasons"] = 1

        team_id_map = self._upsert_teams(conn, teams_api)
        stats["teams"] = len(team_id_map)

        matches_api = self._api.get_matches(code, season_year)
        stats["matches"] = self._upsert_matches(
            conn, matches_api, season_id, team_id_map, code, season_year
        )

        try:
            standings = self._api.get_standings(code, season_year)
            summary = extract_standings_summary(standings)
            if summary:
                log.info("    Standings: %d filas", len(summary))
        except requests.HTTPError as exc:
            if exc.response is not None and exc.response.status_code == 403:
                log.debug("    Standings no disponibles para %s/%s", code, season_year)
            else:
                raise

        return stats

    def _upsert_competition(
        self, conn: PgConnection, code: str, api: dict[str, Any]
    ) -> int:
        existing = self._db.resolve_ref(
            conn, "competition", self._settings.external_source, code
        )
        mapped = map_competition(api)
        if existing:
            with conn.cursor() as cur:
                cur.execute(
                    "UPDATE competitions SET name = %s, type = %s, format = %s WHERE id = %s",
                    (mapped["name"], mapped["type"], mapped["format"], existing),
                )
            return existing

        with conn.cursor() as cur:
            cur.execute(
                "INSERT INTO competitions (name, type, format) VALUES (%s, %s, %s) RETURNING id",
                (mapped["name"], mapped["type"], mapped["format"]),
            )
            internal_id = cur.fetchone()[0]
        self._db.save_ref(conn, "competition", self._settings.external_source, code, internal_id)
        log.info("Competición insertada id=%s code=%s", internal_id, code)
        return internal_id

    def _upsert_season(
        self, conn: PgConnection, code: str, competition_id: int, year: str
    ) -> int:
        ref_key = f"{code}:{year}"
        existing = self._db.resolve_ref(
            conn, "season", self._settings.external_source, ref_key
        )
        if existing:
            return existing

        with conn.cursor() as cur:
            cur.execute(
                "INSERT INTO seasons (competition_id, year) VALUES (%s, %s) RETURNING id",
                (competition_id, year),
            )
            internal_id = cur.fetchone()[0]
        self._db.save_ref(conn, "season", self._settings.external_source, ref_key, internal_id)
        return internal_id

    def _upsert_teams(
        self, conn: PgConnection, teams_api: list[dict[str, Any]]
    ) -> dict[int, int]:
        result: dict[int, int] = {}
        for api_team in teams_api:
            api_id = api_team.get("id")
            if api_id is None:
                continue
            result[api_id] = self._upsert_team(conn, api_team)
        return result

    def _upsert_team(self, conn: PgConnection, api_team: dict[str, Any]) -> int:
        api_id = api_team["id"]
        ext_id = str(api_id)
        mapped = map_team(api_team)
        existing = self._db.resolve_ref(
            conn, "team", self._settings.external_source, ext_id
        )
        if existing:
            with conn.cursor() as cur:
                cur.execute(
                    "UPDATE teams SET name = %s, country = %s, logo_url = %s WHERE id = %s",
                    (mapped["name"], mapped["country"], mapped["logo_url"], existing),
                )
            return existing

        with conn.cursor() as cur:
            cur.execute(
                "INSERT INTO teams (name, country, logo_url) VALUES (%s, %s, %s) RETURNING id",
                (mapped["name"], mapped["country"], mapped["logo_url"]),
            )
            internal_id = cur.fetchone()[0]
        self._db.save_ref(conn, "team", self._settings.external_source, ext_id, internal_id)
        return internal_id

    def _resolve_team_id(
        self,
        conn: PgConnection,
        team_id_map: dict[int, int],
        team_obj: dict[str, Any] | None,
    ) -> int | None:
        if not team_obj or team_obj.get("id") is None:
            return None
        api_id = team_obj["id"]
        if api_id in team_id_map:
            return team_id_map[api_id]
        internal = self._upsert_team(conn, team_obj)
        team_id_map[api_id] = internal
        return internal

    def _upsert_matches(
        self,
        conn: PgConnection,
        matches_api: list[dict[str, Any]],
        season_id: int,
        team_id_map: dict[int, int],
        code: str,
        season_year: str,
    ) -> int:
        count = 0
        for api_match in matches_api:
            api_id = api_match.get("id")
            if api_id is None:
                continue

            home_id = self._resolve_team_id(conn, team_id_map, api_match.get("homeTeam"))
            away_id = self._resolve_team_id(conn, team_id_map, api_match.get("awayTeam"))
            if home_id is None or away_id is None:
                log.warning("Partido %s omitido (equipos incompletos)", api_id)
                continue

            mapped = map_match(api_match, season_id, home_id, away_id)
            ext_id = str(api_id)
            existing = self._db.resolve_ref(
                conn, "match", self._settings.external_source, ext_id
            )

            if existing:
                with conn.cursor() as cur:
                    cur.execute(
                        """
                        UPDATE matches SET
                            season_id = %s, date = %s,
                            home_team_id = %s, away_team_id = %s,
                            home_score = %s, away_score = %s,
                            status = %s, round = %s, stage = %s, "group" = %s
                        WHERE id = %s
                        """,
                        (
                            mapped["season_id"], mapped["date"],
                            mapped["home_team_id"], mapped["away_team_id"],
                            mapped["home_score"], mapped["away_score"],
                            mapped["status"], mapped["round"], mapped["stage"],
                            mapped["group"], existing,
                        ),
                    )
            else:
                with conn.cursor() as cur:
                    cur.execute(
                        """
                        INSERT INTO matches (
                            season_id, date, home_team_id, away_team_id,
                            home_score, away_score, status, round, stage, "group"
                        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                        RETURNING id
                        """,
                        (
                            mapped["season_id"], mapped["date"],
                            mapped["home_team_id"], mapped["away_team_id"],
                            mapped["home_score"], mapped["away_score"],
                            mapped["status"], mapped["round"], mapped["stage"],
                            mapped["group"],
                        ),
                    )
                    internal_id = cur.fetchone()[0]
                self._db.save_ref(
                    conn, "match", self._settings.external_source, ext_id, internal_id
                )
            count += 1

        log.info("    Partidos: %d (%s/%s)", count, code, season_year)
        return count
