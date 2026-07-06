"""Cliente HTTP para football-data.org v4."""

from __future__ import annotations

import logging
import time
from typing import Any

import requests

from .config import Settings

log = logging.getLogger(__name__)


class FootballDataClient:
    def __init__(self, settings: Settings) -> None:
        self._settings = settings
        self._session = requests.Session()
        self._session.headers.update({"X-Auth-Token": settings.api_token})
        self._last_request_at = 0.0

    def _throttle(self) -> None:
        elapsed = time.monotonic() - self._last_request_at
        wait = self._settings.request_delay_seconds - elapsed
        if wait > 0:
            time.sleep(wait)
        self._last_request_at = time.monotonic()

    def _get(self, path: str, params: dict[str, Any] | None = None) -> dict[str, Any]:
        self._throttle()
        url = f"{self._settings.api_base_url}{path}"
        log.info("GET %s params=%s", path, params)
        resp = self._session.get(url, params=params, timeout=30)
        if resp.status_code == 429:
            retry_after = int(resp.headers.get("Retry-After", "60"))
            log.warning("Rate limit alcanzado, esperando %ss", retry_after)
            time.sleep(retry_after)
            return self._get(path, params)
        resp.raise_for_status()
        return resp.json()

    def get_competition(self, code: str) -> dict[str, Any]:
        return self._get(f"/competitions/{code}")

    def get_teams(self, code: str, season_year: str) -> list[dict[str, Any]]:
        data = self._get(f"/competitions/{code}/teams", {"season": season_year})
        return data.get("teams", [])

    def get_matches(self, code: str, season_year: str) -> list[dict[str, Any]]:
        data = self._get(f"/competitions/{code}/matches", {"season": season_year})
        return data.get("matches", [])

    def get_standings(self, code: str, season_year: str) -> dict[str, Any]:
        return self._get(f"/competitions/{code}/standings", {"season": season_year})
