package rndbet.rndbetpredictionsbackend.stattargets.domain;

import java.util.Arrays;
import java.util.Optional;

/** Columnas de {@code team_match_stats} permitidas como objetivo (sin posesión). */
public enum StatMetric {
    GOALS("goals"),
    SHOTS("shots"),
    SHOTS_ON_TARGET("shots_on_target"),
    SAVES("saves"),
    YELLOW_CARDS("yellow_cards"),
    RED_CARDS("red_cards"),
    CORNERS("corners"),
    FOULS("fouls"),
    OFFSIDES("offsides");

    private final String apiValue;

    StatMetric(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }

    public static Optional<StatMetric> fromApiValue(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        String s = raw.trim();
        return Arrays.stream(values()).filter(m -> m.apiValue.equalsIgnoreCase(s)).findFirst();
    }
}
