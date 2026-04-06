package rndbet.rndbetpredictionsbackend.standings.application.port.in;

import rndbet.rndbetpredictionsbackend.standings.domain.StandingRow;

import java.util.List;

public interface GetStandingsUseCase {

    List<StandingRow> getStandings(int competitionId, int seasonId);
}
