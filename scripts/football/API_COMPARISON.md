# Comparativa de APIs gratuitas de fútbol

Documento de investigación para el módulo de ingestión. Fecha: julio 2026.

## Resumen ejecutivo

**API principal elegida: [football-data.org](https://www.football-data.org/)** (plan Free, €0/mo, sin tarjeta).

Motivos: gratuita permanente, 10 req/min, cubre las 12 competiciones principales europeas, endpoints alineados con nuestro esquema (competiciones, equipos, partidos, standings), documentación v4 estable.

**Limitación clave del plan free:** sin estadísticas de partido (corners, posesión, tiros) ni jugadores/alineaciones. Esas tablas se poblarán vía live ingest (scraper existente) en fase 2.

## Comparativa

| API | Costo | Ligas (free) | Rate limit | Fixtures | Standings | Stats partido | Jugadores | Estabilidad |
|---|---|---|---|---|---|---|---|---|
| **football-data.org** | €0 forever | 12 top | 10/min | ✓ (delayed) | ✓ | ✗ (add-on €15) | ✗ (add-on €29) | Alta |
| API-Football (RapidAPI) | €0 tier | 1200+ | 100/día | ✓ | ✓ | ✓ limitado | ✓ limitado | Media |
| TheSportsDB | €0 | 617 | 30/min | ✓ | Parcial | ✗ | Básico | Baja (crowd) |
| OpenLigaDB | €0 | Alemania | — | ✓ | ✓ | Básico | ✗ | Media |
| Sportmonks | €0 | 2 ligas | — | ✓ | ✓ | ✗ | ✗ | Alta (muestra) |
| StatsBomb Open Data | €0 | Muestras | N/A (dataset) | ✓ | ✗ | ✓ xG | ✓ | Alta (offline) |

## Detalle football-data.org (elegida)

### Cobertura free (12 competiciones)

Premier League (PL), La Liga (PD), Serie A (SA), Bundesliga (BL1), Ligue 1 (FL1),
Champions League (CL), Eredivisie (DED), Primeira Liga (PPL), Championship (ELC),
Brasileirão (BSA), Euro (EC), World Cup (WC).

### Endpoints usados

```
GET /v4/competitions/{code}           → competitions
GET /v4/competitions/{code}/teams     → teams
GET /v4/competitions/{code}/matches   → matches
GET /v4/competitions/{code}/standings → validación (backend calcula standings)
```

### Rate limit

10 requests/minute. El script usa delay de 6.5s entre requests (~9/min).

### Mapeo de status API → BD

| API | BD |
|---|---|
| SCHEDULED, TIMED | schedule |
| LIVE, IN_PLAY, PAUSED | live |
| FINISHED, AWARDED | finished |
| POSTPONED | postponed |
| SUSPENDED | suspended |
| CANCELLED | cancelled |

## Estrategia combinada (fase 2)

| Necesidad | Fuente |
|---|---|
| Fixtures, resultados, standings base | football-data.org (free) |
| Stats en vivo, eventos | Scraper existente → `POST /api/v1/live-track/match-state` |
| Logos adicionales | TheSportsDB (opcional, free) |
| xG / analytics | StatsBomb Open Data (offline) o Sportmonks paid |

## Por qué NO API-Football como principal

- 100 req/día es insuficiente para cargar temporadas completas de varias ligas
- Una temporada PL (~380 partidos + 20 equipos + standings) consume ~3-5 requests, pero re-ingestión diaria de múltiples ligas agota el cupo
- Mejor como complemento puntual si se necesita cobertura de ligas fuera del top 12

## Registro

1. Crear cuenta en https://www.football-data.org/client/register
2. Copiar token a `FOOTBALL_DATA_API_TOKEN` en `.env`
