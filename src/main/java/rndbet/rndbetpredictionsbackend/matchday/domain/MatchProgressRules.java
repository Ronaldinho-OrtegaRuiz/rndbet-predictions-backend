package rndbet.rndbetpredictionsbackend.matchday.domain;

import java.util.Locale;
import java.util.Set;

/**
 * Partido “cerrado” para decidir jornada actual: solo lo que la BD ya usa como valor de {@code status}.
 * Cualquier otro valor (p. ej. {@code schedule}) se trata como pendiente.
 */
public final class MatchProgressRules {

    private static final Set<String> CLOSED = Set.of("finished", "suspended", "cancelled");

    private MatchProgressRules() {}

    public static boolean isTerminal(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }
        return CLOSED.contains(status.trim().toLowerCase(Locale.ROOT));
    }
}
