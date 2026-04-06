package rndbet.rndbetpredictionsbackend.matchday.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rndbet.rndbetpredictionsbackend.matchday.application.port.in.GetMatchdayFixturesUseCase;
import rndbet.rndbetpredictionsbackend.matchday.application.port.out.LoadMatchdayFixturesPort;
import rndbet.rndbetpredictionsbackend.matchday.domain.CurrentMatchday;
import rndbet.rndbetpredictionsbackend.matchday.domain.CurrentMatchdayResolver;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;
import rndbet.rndbetpredictionsbackend.standings.application.exception.SeasonNotFoundException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetMatchdayFixturesService implements GetMatchdayFixturesUseCase {

    private final LoadMatchdayFixturesPort loadMatchdayFixturesPort;

    @Override
    public List<MatchdayFixture> getFixtures(int competitionId, int seasonId, int round) {
        if (!loadMatchdayFixturesPort.seasonBelongsToCompetition(competitionId, seasonId)) {
            throw new SeasonNotFoundException(
                    "La temporada %d no existe o no pertenece a la competición %d.".formatted(seasonId, competitionId));
        }
        return loadMatchdayFixturesPort.loadMatchesForRound(seasonId, round);
    }

    @Override
    public CurrentMatchday getCurrentFixtures(int competitionId, int seasonId) {
        if (!loadMatchdayFixturesPort.seasonBelongsToCompetition(competitionId, seasonId)) {
            throw new SeasonNotFoundException(
                    "La temporada %d no existe o no pertenece a la competición %d.".formatted(seasonId, competitionId));
        }
        Map<Integer, List<String>> byRound = loadMatchdayFixturesPort.loadMatchStatusesByRound(seasonId);
        CurrentMatchdayResolver.Resolution resolution = CurrentMatchdayResolver.resolve(byRound);
        if (resolution.round() == null) {
            return new CurrentMatchday(null, false, List.of());
        }
        List<MatchdayFixture> fixtures =
                loadMatchdayFixturesPort.loadMatchesForRound(seasonId, resolution.round());
        return new CurrentMatchday(resolution.round(), resolution.seasonCompleted(), fixtures);
    }
}
