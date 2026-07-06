-- Restringir ejecución de football_clean_all_data() a roles de backend solamente.

REVOKE ALL ON FUNCTION public.football_clean_all_data() FROM PUBLIC;
REVOKE ALL ON FUNCTION public.football_clean_all_data() FROM anon, authenticated;

GRANT EXECUTE ON FUNCTION public.football_clean_all_data() TO postgres;
GRANT EXECUTE ON FUNCTION public.football_clean_all_data() TO service_role;
