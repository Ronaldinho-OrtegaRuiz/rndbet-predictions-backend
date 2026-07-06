#!/usr/bin/env python3
"""CLI del módulo de ingestión de fútbol."""

from __future__ import annotations

import argparse
import logging
import os
import sys

from football_ingest.clean import run_clean
from football_ingest.config import DEFAULT_COMPETITIONS, load_settings
from football_ingest.ingest import FootballIngestService


def _setup_logging(verbose: bool) -> None:
    level = logging.DEBUG if verbose else logging.INFO
    logging.basicConfig(
        level=level,
        format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Ingestión de fútbol desde football-data.org → Supabase PostgreSQL"
    )
    sub = parser.add_subparsers(dest="command", required=True)

    sub.add_parser("clean", help="Vacía datos del módulo de fútbol (conserva estructura)")

    sub.add_parser(
        "ingest-all",
        help="Carga todas las ligas top + Champions desde FOOTBALL_SEASON_FROM (default 2020)",
    )

    ingest_parser = sub.add_parser("ingest", help="Carga UNA competición/temporada")
    ingest_parser.add_argument("--competition", help="Código: PL, PD, SA, BL1, FL1, CL")
    ingest_parser.add_argument("--season", help="Año inicio temporada, ej. 2024")

    sub.add_parser("check", help="Verifica conexión a BD y token API")

    parser.add_argument("-v", "--verbose", action="store_true")
    args = parser.parse_args()
    _setup_logging(args.verbose)

    require_token = args.command in ("ingest", "ingest-all", "check")
    try:
        settings = load_settings(require_api_token=require_token)
    except RuntimeError as exc:
        logging.error(str(exc))
        return 1

    if args.command == "clean":
        run_clean(settings)
        return 0

    if args.command == "check":
        from football_ingest.db import FootballDb
        from football_ingest.football_data_client import FootballDataClient

        db = FootballDb(settings.database_url)
        with db.connect() as conn:
            with conn.cursor() as cur:
                cur.execute("SELECT 1")
        logging.info("Conexión BD: OK (usa SUPABASE_DB_PASSWORD del .env raíz)")

        if settings.api_token:
            client = FootballDataClient(settings)
            comp = client.get_competition("PL")
            logging.info("API football-data.org: OK — %s", comp.get("name"))
            logging.info(
                "Competiciones configuradas: %s",
                ", ".join(f"{k} ({v})" for k, v in settings.competitions.items()),
            )
            logging.info("Temporadas desde: %s", settings.season_from)
        else:
            logging.warning("FOOTBALL_DATA_API_TOKEN vacío")
        return 0

    if args.command == "ingest-all":
        logging.info(
            "Modo masivo: %d competiciones, temporadas desde %s",
            len(settings.competitions),
            settings.season_from,
        )
        FootballIngestService(settings).run_all()
        return 0

    if args.command == "ingest":
        if args.competition:
            os.environ["FOOTBALL_COMPETITION_CODE"] = args.competition
        if args.season:
            os.environ["FOOTBALL_SEASON_YEAR"] = args.season
        settings = load_settings(require_api_token=True)
        FootballIngestService(settings).run()
        return 0

    return 1


if __name__ == "__main__":
    sys.exit(main())
