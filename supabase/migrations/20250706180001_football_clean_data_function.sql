-- Limpieza controlada del módulo de fútbol.
-- Vacía datos sin destruir estructura, constraints ni índices.
-- Incluye user_match_stat_targets (depende de matches) pero NO toca users.

CREATE OR REPLACE FUNCTION public.football_clean_all_data()
RETURNS TABLE (table_name text, rows_deleted bigint)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    counts record;
BEGIN
    -- Conteo previo (informativo)
    CREATE TEMP TABLE _football_clean_log (tbl text, cnt bigint) ON COMMIT DROP;

    INSERT INTO _football_clean_log VALUES
        ('prediction_evaluations', (SELECT count(*) FROM prediction_evaluations)),
        ('predictions', (SELECT count(*) FROM predictions)),
        ('user_match_stat_targets', (SELECT count(*) FROM user_match_stat_targets)),
        ('match_events', (SELECT count(*) FROM match_events)),
        ('team_match_stats', (SELECT count(*) FROM team_match_stats)),
        ('matches', (SELECT count(*) FROM matches)),
        ('players', (SELECT count(*) FROM players)),
        ('seasons', (SELECT count(*) FROM seasons)),
        ('teams', (SELECT count(*) FROM teams)),
        ('competitions', (SELECT count(*) FROM competitions)),
        ('football_api_refs', (SELECT count(*) FROM football_api_refs));

    TRUNCATE TABLE
        prediction_evaluations,
        predictions,
        user_match_stat_targets,
        match_events,
        team_match_stats,
        matches,
        players,
        seasons,
        teams,
        competitions,
        football_api_refs
    RESTART IDENTITY;

    FOR counts IN SELECT tbl, cnt FROM _football_clean_log ORDER BY tbl LOOP
        table_name := counts.tbl;
        rows_deleted := counts.cnt;
        RETURN NEXT;
    END LOOP;
END;
$$;

COMMENT ON FUNCTION public.football_clean_all_data() IS
    'Vacía todas las tablas del módulo de fútbol en orden seguro por FK. No toca users.';
