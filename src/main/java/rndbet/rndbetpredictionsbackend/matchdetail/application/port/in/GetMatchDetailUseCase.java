package rndbet.rndbetpredictionsbackend.matchdetail.application.port.in;

import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;

public interface GetMatchDetailUseCase {

    MatchDetail getMatchDetail(int competitionId, int seasonId, int round, int matchId);
}
