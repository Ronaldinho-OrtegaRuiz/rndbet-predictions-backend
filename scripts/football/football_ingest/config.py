"""Configuración del módulo de ingestión de fútbol."""

from __future__ import annotations

import os
from dataclasses import dataclass, field
from pathlib import Path

from dotenv import load_dotenv

# Carga .env del proyecto raíz (mismo que Spring Boot) y opcionalmente scripts/football/.env
_ROOT = Path(__file__).resolve().parents[3]
load_dotenv(_ROOT / ".env")
load_dotenv(Path(__file__).resolve().parents[1] / ".env")

# Códigos football-data.org v4 — las 6 ligas top + Champions (Europa League = EL requiere plan pago)
DEFAULT_COMPETITIONS: dict[str, str] = {
    "PL": "Premier League",
    "PD": "La Liga",
    "SA": "Serie A",
    "BL1": "Bundesliga",
    "FL1": "Ligue 1",
    "CL": "Champions League",
}

DEFAULT_SEASON_FROM = 2020


@dataclass(frozen=True)
class Settings:
    api_token: str
    database_url: str
    competition_code: str
    season_year: str
    competitions: dict[str, str] = field(default_factory=lambda: dict(DEFAULT_COMPETITIONS))
    season_from: int = DEFAULT_SEASON_FROM
    season_to: int | None = None  # None = hasta temporada actual de la API
    api_base_url: str = "https://api.football-data.org/v4"
    external_source: str = "football-data.org"
    request_delay_seconds: float = 6.5


def build_database_url() -> str:
    """Usa DATABASE_URL si existe; si no, arma la URL con SUPABASE_DB_PASSWORD (como Spring Boot)."""
    explicit = os.getenv("DATABASE_URL", "").strip()
    if explicit:
        return explicit

    password = os.getenv("SUPABASE_DB_PASSWORD", "").strip()
    if not password:
        raise RuntimeError(
            "Configura SUPABASE_DB_PASSWORD en el .env raíz del proyecto "
            "(o DATABASE_URL en scripts/football/.env)"
        )

    host = os.getenv("SUPABASE_DB_HOST", "aws-0-us-west-2.pooler.supabase.com").strip()
    user = os.getenv("SUPABASE_DB_USER", "postgres.xlswyviswmgyqjcprnmb").strip()
    port = os.getenv("SUPABASE_DB_PORT", "5432").strip()
    dbname = os.getenv("SUPABASE_DB_NAME", "postgres").strip()
    return f"postgresql://{user}:{password}@{host}:{port}/{dbname}?sslmode=require"


def load_settings(*, require_api_token: bool = True) -> Settings:
    token = os.getenv("FOOTBALL_DATA_API_TOKEN", "").strip()
    if require_api_token and not token:
        raise RuntimeError(
            "FOOTBALL_DATA_API_TOKEN no configurado. "
            "Agrégalo al .env raíz del proyecto (registro en football-data.org)."
        )

    season_from = int(os.getenv("FOOTBALL_SEASON_FROM", str(DEFAULT_SEASON_FROM)))
    season_to_raw = os.getenv("FOOTBALL_SEASON_TO", "").strip()
    season_to = int(season_to_raw) if season_to_raw else None

    competitions_raw = os.getenv("FOOTBALL_COMPETITIONS", "").strip()
    if competitions_raw:
        codes = [c.strip().upper() for c in competitions_raw.split(",") if c.strip()]
        competitions = {c: DEFAULT_COMPETITIONS.get(c, c) for c in codes}
    else:
        competitions = dict(DEFAULT_COMPETITIONS)

    return Settings(
        api_token=token,
        database_url=build_database_url(),
        competition_code=os.getenv("FOOTBALL_COMPETITION_CODE", "PL").strip().upper(),
        season_year=os.getenv("FOOTBALL_SEASON_YEAR", "2024").strip(),
        competitions=competitions,
        season_from=season_from,
        season_to=season_to,
    )
