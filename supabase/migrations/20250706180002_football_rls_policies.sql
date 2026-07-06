-- Políticas RLS del módulo de fútbol.
-- El backend Spring conecta como rol postgres (bypass RLS).
-- Estas políticas protegen acceso vía Supabase client (anon/authenticated).

-- ── Habilitar RLS ──────────────────────────────────────────────────────────

ALTER TABLE public.competitions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.seasons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.teams ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.matches ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.team_match_stats ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.players ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.match_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.predictions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.prediction_evaluations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.football_api_refs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_match_stat_targets ENABLE ROW LEVEL SECURITY;

-- ── Lectura pública de datos de referencia de fútbol ───────────────────────

CREATE POLICY football_competitions_public_read
    ON public.competitions FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_seasons_public_read
    ON public.seasons FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_teams_public_read
    ON public.teams FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_matches_public_read
    ON public.matches FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_team_match_stats_public_read
    ON public.team_match_stats FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_players_public_read
    ON public.players FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_match_events_public_read
    ON public.match_events FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_predictions_public_read
    ON public.predictions FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_prediction_evaluations_public_read
    ON public.prediction_evaluations FOR SELECT TO anon, authenticated USING (true);

CREATE POLICY football_api_refs_public_read
    ON public.football_api_refs FOR SELECT TO anon, authenticated USING (true);

-- ── user_match_stat_targets: sin acceso directo desde cliente Supabase ───────
-- La app usa JWT propio (tabla users) vía backend Spring, no Supabase Auth.
-- Sin policies = todo bloqueado para anon/authenticated.

-- ── Revocar escritura directa desde roles de cliente ───────────────────────
-- (RLS sin policy INSERT/UPDATE/DELETE ya bloquea; revocamos grants explícitos)

REVOKE INSERT, UPDATE, DELETE, TRUNCATE ON
    public.competitions,
    public.seasons,
    public.teams,
    public.matches,
    public.team_match_stats,
    public.players,
    public.match_events,
    public.predictions,
    public.prediction_evaluations,
    public.football_api_refs,
    public.user_match_stat_targets
FROM anon, authenticated;

-- Mantener SELECT para datos públicos de fútbol
GRANT SELECT ON
    public.competitions,
    public.seasons,
    public.teams,
    public.matches,
    public.team_match_stats,
    public.players,
    public.match_events,
    public.predictions,
    public.prediction_evaluations,
    public.football_api_refs
TO anon, authenticated;
