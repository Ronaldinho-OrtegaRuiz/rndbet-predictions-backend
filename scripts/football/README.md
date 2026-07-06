# Módulo de ingestión de fútbol

Scripts Python para limpiar y poblar la base de datos del módulo de fútbol usando [football-data.org](https://www.football-data.org/) (plan gratuito).

## Requisitos

- Python 3.10+
- Token gratuito de football-data.org
- `DATABASE_URL` apuntando a Supabase (misma conexión que el backend Spring)

## Configuración

```bash
cd scripts/football
python -m venv .venv
.venv\Scripts\activate        # Windows
pip install -r requirements.txt
copy .env.example .env        # editar con credenciales
```

Variables en `.env`:

| Variable | Descripción |
|---|---|
| `FOOTBALL_DATA_API_TOKEN` | Token de football-data.org |
| `DATABASE_URL` | `postgresql://postgres.xlswyviswmgyqjcprnmb:PASSWORD@aws-0-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require` |
| `FOOTBALL_COMPETITION_CODE` | Código de liga: `PL`, `PD`, `SA`, `BL1`, `FL1`, `CL`, etc. |
| `FOOTBALL_SEASON_YEAR` | Año de inicio de temporada (ej. `2024` = 2024/25) |

## Uso

```bash
# Limpiar datos de fútbol (conserva estructura)
python main.py clean

# Cargar competición/temporada
python main.py ingest
python main.py ingest --competition CL --season 2024
```

## Mapeo API → BD

| Tabla BD | Fuente API | Endpoint |
|---|---|---|
| `competitions` | Competición | `GET /v4/competitions/{code}` |
| `seasons` | Temporada actual | derivado de `currentSeason.startDate` |
| `teams` | Equipos | `GET /v4/competitions/{code}/teams?season={year}` |
| `matches` | Partidos | `GET /v4/competitions/{code}/matches?season={year}` |
| `football_api_refs` | IDs externos | generado en ingestión (idempotente) |
| Standings | Validación | `GET /v4/competitions/{code}/standings` — el backend calcula tabla desde partidos finalizados |

**No disponibles en plan gratuito** (segunda fase / live ingest):

| Tabla BD | Alternativa |
|---|---|
| `team_match_stats` | Scraper live ingest o add-on Statistics (€15/mo) |
| `players` | Add-on Deep Data (€29/mo) o manual |
| `match_events` | Live ingest del scraper existente |
| `predictions` | Pipeline ML interno (no API) |

## Migraciones SQL

Las migraciones están en `supabase/migrations/`:

1. `20250706180000_football_api_refs.sql` — tabla de mapeo de IDs
2. `20250706180001_football_clean_data_function.sql` — función `football_clean_all_data()`
3. `20250706180002_football_rls_policies.sql` — políticas RLS

Ejecutar limpieza desde SQL:

```sql
SELECT * FROM football_clean_all_data();
```

## API elegida: football-data.org

Ver comparativa completa en `API_COMPARISON.md`.
