package rndbet.rndbetpredictionsbackend.stattargets.domain;

import java.util.Arrays;
import java.util.Optional;

public enum TargetState {
    PENDING,
    FULFILLED,
    FAILED;

    public static Optional<TargetState> fromDb(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        String s = raw.trim().toUpperCase();
        return Arrays.stream(values()).filter(v -> v.name().equals(s)).findFirst();
    }
}
