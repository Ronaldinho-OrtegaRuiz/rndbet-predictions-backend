package rndbet.rndbetpredictionsbackend.stattargets.application.port.out;

import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;
import rndbet.rndbetpredictionsbackend.stattargets.domain.StatMetric;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetScope;

import java.util.List;
import java.util.Optional;

public interface UserMatchStatTargetsPort {

    List<MatchStatTarget> listByUserAndMatch(long userId, int matchId);

    Optional<MatchStatTarget> findByIdAndUser(long targetId, long userId);

    long insert(long userId, int matchId, TargetScope scope, StatMetric stat, int threshold);

    boolean updateThresholdIfPending(long targetId, long userId, int matchId, int newThreshold);

    boolean deleteByIdUserAndMatch(long targetId, long userId, int matchId);

    List<MatchStatTarget> listPendingByMatchId(int matchId);

    boolean markFulfilledIfPending(long targetId, Integer matchMinute);

    boolean markFailedIfPending(long targetId);
}
