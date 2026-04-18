package rndbet.rndbetpredictionsbackend.stattargets.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import rndbet.rndbetpredictionsbackend.jpa.entity.UserMatchStatTargetEntity;
import rndbet.rndbetpredictionsbackend.jpa.repository.UserMatchStatTargetRepository;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.out.UserMatchStatTargetsPort;
import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;
import rndbet.rndbetpredictionsbackend.stattargets.domain.StatMetric;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetScope;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetState;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MatchStatTargetsJpaAdapter implements UserMatchStatTargetsPort {

    private final UserMatchStatTargetRepository targetRepository;

    @Override
    public List<MatchStatTarget> listByUserAndMatch(long userId, int matchId) {
        return targetRepository.findByUserIdAndMatchIdOrderByIdAsc(userId, matchId).stream()
                .map(MatchStatTargetsJpaAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<MatchStatTarget> findByIdAndUser(long targetId, long userId) {
        return targetRepository.findByIdAndUserId(targetId, userId).map(MatchStatTargetsJpaAdapter::toDomain);
    }

    @Override
    public long insert(long userId, int matchId, TargetScope scope, StatMetric stat, int threshold) {
        UserMatchStatTargetEntity e = new UserMatchStatTargetEntity();
        e.setUserId(userId);
        e.setMatchId(matchId);
        e.setScope(scope.name());
        e.setStat(stat.apiValue());
        e.setThreshold(threshold);
        UserMatchStatTargetEntity saved = targetRepository.saveAndFlush(e);
        return saved.getId();
    }

    @Override
    public boolean updateThresholdIfPending(long targetId, long userId, int matchId, int newThreshold) {
        Optional<UserMatchStatTargetEntity> opt = targetRepository.findByIdAndUserId(targetId, userId);
        if (opt.isEmpty()) {
            return false;
        }
        UserMatchStatTargetEntity e = opt.get();
        if (e.getMatchId() != matchId || !"PENDING".equals(e.getState())) {
            return false;
        }
        e.setThreshold(newThreshold);
        targetRepository.save(e);
        return true;
    }

    @Override
    public boolean deleteByIdUserAndMatch(long targetId, long userId, int matchId) {
        return targetRepository.deleteByIdAndUserIdAndMatchId(targetId, userId, matchId) > 0;
    }

    private static MatchStatTarget toDomain(UserMatchStatTargetEntity e) {
        return new MatchStatTarget(
                e.getId(),
                e.getUserId(),
                e.getMatchId(),
                TargetScope.fromApiValue(e.getScope())
                        .orElseThrow(() -> new IllegalStateException("scope inválido en BD: " + e.getScope())),
                StatMetric.fromApiValue(e.getStat())
                        .orElseThrow(() -> new IllegalStateException("stat inválido en BD: " + e.getStat())),
                e.getThreshold(),
                TargetState.fromDb(e.getState())
                        .orElseThrow(() -> new IllegalStateException("state inválido en BD: " + e.getState())),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getFulfilledAt(),
                e.getFulfilledMatchMinute(),
                e.getFailedAt());
    }
}
