package rndbet.rndbetpredictionsbackend.standings.domain;

import java.util.List;

public record StandingRow(
        int position,
        int teamId,
        String teamName,
        String teamLogoUrl,
        int played,
        int won,
        int drawn,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        int points,
        List<FormLetter> form
) {
}
