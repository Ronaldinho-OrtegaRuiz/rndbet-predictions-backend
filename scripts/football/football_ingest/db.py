"""Acceso a PostgreSQL para ingestión de fútbol."""

from __future__ import annotations

from contextlib import contextmanager
from typing import Generator, Optional

import psycopg2
from psycopg2.extensions import connection as PgConnection


class FootballDb:
    def __init__(self, database_url: str) -> None:
        self._database_url = database_url

    @contextmanager
    def connect(self) -> Generator[PgConnection, None, None]:
        conn = psycopg2.connect(self._database_url)
        try:
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise
        finally:
            conn.close()

    def resolve_ref(
        self,
        conn: PgConnection,
        entity_type: str,
        external_source: str,
        external_id: str,
    ) -> Optional[int]:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT internal_id FROM football_api_refs
                WHERE entity_type = %s AND external_source = %s AND external_id = %s
                """,
                (entity_type, external_source, external_id),
            )
            row = cur.fetchone()
            return row[0] if row else None

    def save_ref(
        self,
        conn: PgConnection,
        entity_type: str,
        external_source: str,
        external_id: str,
        internal_id: int,
    ) -> None:
        with conn.cursor() as cur:
            cur.execute(
                """
                INSERT INTO football_api_refs (entity_type, external_source, external_id, internal_id)
                VALUES (%s, %s, %s, %s)
                ON CONFLICT (entity_type, external_source, external_id) DO UPDATE
                SET internal_id = EXCLUDED.internal_id
                """,
                (entity_type, external_source, external_id, internal_id),
            )
