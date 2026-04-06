package rndbet.rndbetpredictionsbackend.matchdetail.application.port.out;

import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;

import java.util.Optional;

public interface LoadMatchDetailPort {

    boolean seasonBelongsToCompetition(int competitionId, int seasonId);

    Optional<MatchDetail> loadMatchDetail(int competitionId, int seasonId, int round, int matchId);
}
