package rndbet.rndbetpredictionsbackend.matchday.domain;

import java.util.List;
import java.util.Map;

/**
 * Jornada “en curso”: la menor {@code round} que aún no está totalmente terminal,
 * siempre que todas las jornadas con número menor ya estén totalmente terminales.
 * Si todas las jornadas están cerradas, devuelve la última jornada y {@code seasonCompleted=true}.
 */
public final class CurrentMatchdayResolver {

    public record Resolution(Integer round, boolean seasonCompleted) {}

    private CurrentMatchdayResolver() {}

    public static Resolution resolve(Map<Integer, List<String>> statusesByRound) {
        if (statusesByRound.isEmpty()) {
            return new Resolution(null, false);
        }
        List<Integer> rounds = statusesByRound.keySet().stream().sorted().toList();

        for (int round : rounds) {
            List<String> statuses = statusesByRound.get(round);
            if (statuses == null || statuses.isEmpty()) {
                continue;
            }
            boolean allTerminal = statuses.stream().allMatch(MatchProgressRules::isTerminal);
            if (!allTerminal && allPreviousRoundsTerminal(rounds, round, statusesByRound)) {
                return new Resolution(round, false);
            }
        }

        int lastRound = rounds.get(rounds.size() - 1);
        return new Resolution(lastRound, true);
    }

    private static boolean allPreviousRoundsTerminal(
            List<Integer> roundsAsc, int round, Map<Integer, List<String>> statusesByRound) {
        for (int r : roundsAsc) {
            if (r >= round) {
                break;
            }
            List<String> st = statusesByRound.get(r);
            if (st == null || st.isEmpty() || !st.stream().allMatch(MatchProgressRules::isTerminal)) {
                return false;
            }
        }
        return true;
    }
}
