package rndbet.rndbetpredictionsbackend.stattargets.domain;

import java.util.Arrays;
import java.util.Optional;

public enum TargetScope {
    GLOBAL,
    HOME,
    AWAY;

    public static Optional<TargetScope> fromApiValue(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        String s = raw.trim().toUpperCase();
        return Arrays.stream(values()).filter(v -> v.name().equals(s)).findFirst();
    }
}
