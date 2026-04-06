package rndbet.rndbetpredictionsbackend.matchday.domain;

import java.time.Instant;

public record MatchdayFixture(
        int matchId,
        Instant date,
        String status,
        String homeTeamName,
        String homeTeamLogoUrl,
        Integer homeScore,
        String awayTeamName,
        String awayTeamLogoUrl,
        Integer awayScore,
        int homeRedCards,
        int awayRedCards
) {
}
