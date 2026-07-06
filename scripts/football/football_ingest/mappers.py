"""Mapeo football-data.org -> esquema PostgreSQL del backend."""

from __future__ import annotations

from typing import Any, Optional


# football-data.org status -> status en BD (minúsculas, convención del backend)
_STATUS_MAP = {
    "SCHEDULED": "schedule",
    "TIMED": "schedule",
    "LIVE": "live",
    "IN_PLAY": "live",
    "PAUSED": "live",
    "FINISHED": "finished",
    "POSTPONED": "postponed",
    "SUSPENDED": "suspended",
    "CANCELLED": "cancelled",
    "AWARDED": "finished",
}


def map_status(api_status: Optional[str]) -> Optional[str]:
    if not api_status:
        return None
    return _STATUS_MAP.get(api_status.upper(), api_status.lower())


def map_stage(api_stage: Optional[str]) -> Optional[str]:
    if not api_stage:
        return None
    return api_stage.lower()


def map_competition(api: dict[str, Any]) -> dict[str, Any]:
    return {
        "name": api.get("name"),
        "type": api.get("type"),
        "format": _infer_format(api),
    }


def _infer_format(api: dict[str, Any]) -> Optional[str]:
    comp_type = (api.get("type") or "").upper()
    if comp_type == "LEAGUE":
        return "league"
    if comp_type == "CUP":
        return "cup"
    return comp_type.lower() if comp_type else None


def map_team(api: dict[str, Any]) -> dict[str, Any]:
    area = api.get("area") or {}
    return {
        "name": api.get("name") or api.get("shortName"),
        "country": area.get("name"),
        "logo_url": api.get("crest"),
    }


def map_season_year(api_competition: dict[str, Any], fallback: str) -> str:
    current = api_competition.get("currentSeason") or {}
    start = current.get("startDate")
    if start and len(start) >= 4:
        return start[:4]
    return fallback


def map_match(api: dict[str, Any], season_id: int, home_team_id: int, away_team_id: int) -> dict[str, Any]:
    score = api.get("score") or {}
    full_time = score.get("fullTime") or {}
    home_score = full_time.get("home")
    away_score = full_time.get("away")

    # Partidos no finalizados pueden no tener marcador
    if api.get("status") not in ("FINISHED", "AWARDED"):
        home_score = None
        away_score = None

    group = api.get("group")
    if isinstance(group, str) and group.startswith("GROUP_"):
        group = group.replace("GROUP_", "")

    return {
        "season_id": season_id,
        "date": api.get("utcDate"),
        "home_team_id": home_team_id,
        "away_team_id": away_team_id,
        "home_score": home_score,
        "away_score": away_score,
        "status": map_status(api.get("status")),
        "round": api.get("matchday"),
        "stage": map_stage(api.get("stage")),
        "group": group,
    }


def extract_standings_summary(standings_payload: dict[str, Any]) -> list[dict[str, Any]]:
    """Extrae tabla de posiciones (solo logging/validación; el backend calcula standings)."""
    result: list[dict[str, Any]] = []
    for block in standings_payload.get("standings", []):
        table_type = block.get("type")
        for row in block.get("table", []):
            team = row.get("team") or {}
            result.append(
                {
                    "table_type": table_type,
                    "position": row.get("position"),
                    "team_name": team.get("name"),
                    "played": row.get("playedGames"),
                    "won": row.get("won"),
                    "draw": row.get("draw"),
                    "lost": row.get("lost"),
                    "points": row.get("points"),
                    "goal_diff": row.get("goalDifference"),
                }
            )
    return result
