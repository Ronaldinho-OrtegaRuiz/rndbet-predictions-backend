"""Limpieza controlada de datos del módulo de fútbol."""

from __future__ import annotations

import logging

from .config import Settings
from .db import FootballDb

log = logging.getLogger(__name__)


def run_clean(settings: Settings) -> None:
    db = FootballDb(settings.database_url)
    with db.connect() as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT * FROM football_clean_all_data()")
            rows = cur.fetchall()
    log.info("Limpieza completada:")
    for table_name, rows_deleted in rows:
        log.info("  %s: %d filas eliminadas", table_name, rows_deleted)
