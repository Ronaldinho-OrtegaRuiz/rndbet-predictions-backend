package rndbet.rndbetpredictionsbackend.standings.application.port.out;

import rndbet.rndbetpredictionsbackend.standings.domain.FinishedMatchForStandings;

import java.util.List;

public interface LoadStandingsDataPort {

    boolean seasonBelongsToCompetition(int competitionId, int seasonId);

    List<FinishedMatchForStandings> loadFinishedMatchesBySeason(int seasonId);
}
