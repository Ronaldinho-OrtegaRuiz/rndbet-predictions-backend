package rndbet.rndbetpredictionsbackend.matchday.domain;

import java.time.Instant;

public record MatchdayFixture(
        int matchId,
        Instant date,
        String status,
        String homeTeamName,
        Integer homeScore,
        String awayTeamName,
        Integer awayScore,
        int homeRedCards,
        int awayRedCards
) {
}
