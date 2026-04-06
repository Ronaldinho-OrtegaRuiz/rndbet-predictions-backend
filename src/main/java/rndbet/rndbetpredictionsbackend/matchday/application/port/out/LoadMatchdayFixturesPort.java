package rndbet.rndbetpredictionsbackend.matchday.application.port.out;

import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.util.List;
import java.util.Map;

public interface LoadMatchdayFixturesPort {

    boolean seasonBelongsToCompetition(int competitionId, int seasonId);

    Map<Integer, List<String>> loadMatchStatusesByRound(int seasonId);

    List<MatchdayFixture> loadMatchesForRound(int seasonId, int round);
}
