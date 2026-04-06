package rndbet.rndbetpredictionsbackend.standings.domain;

import java.time.Instant;

/**
 * Partido ya disputado con marcador, usado solo para calcular la clasificación.
 */
public record FinishedMatchForStandings(
        int id,
        Instant date,
        int homeTeamId,
        String homeTeamName,
        String homeTeamLogoUrl,
        int awayTeamId,
        String awayTeamName,
        String awayTeamLogoUrl,
        int homeGoals,
        int awayGoals
) {
}
