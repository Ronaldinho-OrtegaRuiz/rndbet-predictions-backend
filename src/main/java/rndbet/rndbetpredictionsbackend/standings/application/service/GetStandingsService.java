package rndbet.rndbetpredictionsbackend.standings.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rndbet.rndbetpredictionsbackend.standings.application.exception.SeasonNotFoundException;
import rndbet.rndbetpredictionsbackend.standings.application.port.in.GetStandingsUseCase;
import rndbet.rndbetpredictionsbackend.standings.application.port.out.LoadStandingsDataPort;
import rndbet.rndbetpredictionsbackend.standings.domain.FinishedMatchForStandings;
import rndbet.rndbetpredictionsbackend.standings.domain.StandingRow;
import rndbet.rndbetpredictionsbackend.standings.domain.StandingsCalculator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetStandingsService implements GetStandingsUseCase {

    private final LoadStandingsDataPort loadStandingsDataPort;

    @Override
    public List<StandingRow> getStandings(int competitionId, int seasonId) {
        if (!loadStandingsDataPort.seasonBelongsToCompetition(competitionId, seasonId)) {
            throw new SeasonNotFoundException(
                    "La temporada %d no existe o no pertenece a la competición %d.".formatted(seasonId, competitionId));
        }
        List<FinishedMatchForStandings> matches = loadStandingsDataPort.loadFinishedMatchesBySeason(seasonId);
        return StandingsCalculator.compute(matches);
    }
}
