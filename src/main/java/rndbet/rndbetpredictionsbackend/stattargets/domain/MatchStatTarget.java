package rndbet.rndbetpredictionsbackend.stattargets.domain;

import java.time.OffsetDateTime;

public record MatchStatTarget(
        long id,
        long userId,
        int matchId,
        TargetScope scope,
        StatMetric stat,
        int threshold,
        TargetState state,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime fulfilledAt,
        Integer fulfilledMatchMinute,
        OffsetDateTime failedAt) {}
