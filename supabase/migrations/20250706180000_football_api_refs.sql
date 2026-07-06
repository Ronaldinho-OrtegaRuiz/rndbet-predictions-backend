-- Tabla auxiliar del módulo de fútbol para mapear IDs externos (p. ej. football-data.org)
-- sin alterar el esquema principal. Permite re-ingestión idempotente.

CREATE TABLE IF NOT EXISTS public.football_api_refs (
    entity_type    text NOT NULL CHECK (entity_type IN ('competition', 'season', 'team', 'match', 'player')),
    external_source text NOT NULL DEFAULT 'football-data.org',
    external_id    text NOT NULL,
    internal_id    integer NOT NULL,
    created_at     timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (entity_type, external_source, external_id)
);

CREATE INDEX IF NOT EXISTS football_api_refs_internal_idx
    ON public.football_api_refs (entity_type, internal_id);

COMMENT ON TABLE public.football_api_refs IS
    'Mapeo external_id -> internal_id para ingestión desde APIs de fútbol.';
