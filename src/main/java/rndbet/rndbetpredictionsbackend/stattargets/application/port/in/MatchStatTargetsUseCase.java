package rndbet.rndbetpredictionsbackend.stattargets.application.port.in;

import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;

import java.util.List;

public interface MatchStatTargetsUseCase {

    List<MatchStatTarget> list(
            long userId, int competitionId, int seasonId, int round, int matchId);

    MatchStatTarget create(
            long userId,
            int competitionId,
            int seasonId,
            int round,
            int matchId,
            String statApiValue,
            String scopeApiValue,
            int threshold);

    MatchStatTarget updateThreshold(
            long userId,
            int competitionId,
            int seasonId,
            int round,
            int matchId,
            long targetId,
            int newThreshold);

    void delete(
            long userId,
            int competitionId,
            int seasonId,
            int round,
            int matchId,
            long targetId);
}
