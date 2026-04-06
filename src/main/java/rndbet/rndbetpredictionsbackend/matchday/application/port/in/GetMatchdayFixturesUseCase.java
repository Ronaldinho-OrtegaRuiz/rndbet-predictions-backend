package rndbet.rndbetpredictionsbackend.matchday.application.port.in;

import rndbet.rndbetpredictionsbackend.matchday.domain.CurrentMatchday;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.util.List;

public interface GetMatchdayFixturesUseCase {

    List<MatchdayFixture> getFixtures(int competitionId, int seasonId, int round);

    CurrentMatchday getCurrentFixtures(int competitionId, int seasonId);
}
