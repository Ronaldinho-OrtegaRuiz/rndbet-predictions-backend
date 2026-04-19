package rndbet.rndbetpredictionsbackend.livetrack;

import java.time.OffsetDateTime;

/**
 * Fila seleccionada para armar el payload hacia el scraper.
 * {@code jornada} = {@code matches.round} (número de jornada/ronda cuando aplica).
 * {@code fase} = {@code matches.stage} (ej. fase de grupos, octavos, cuartos en Champions/Europa League).
 * {@code grupo} = {@code matches.group} (ej. grupo A/B en liga de grupos).
 */
public record LiveTrackMatchOutbound(
        int matchId,
        OffsetDateTime fecha,
        Integer jornada,
        String fase,
        String grupo,
        String competicion,
        String equipoLocal,
        String equipoVisitante) {}
