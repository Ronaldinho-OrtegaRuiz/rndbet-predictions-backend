package rndbet.rndbetpredictionsbackend.standings.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StandingsCalculator {

    private StandingsCalculator() {
    }

    public static List<StandingRow> compute(List<FinishedMatchForStandings> matchesOrderedByDate) {
        Map<Integer, TeamAccumulator> byTeam = new HashMap<>();

        for (FinishedMatchForStandings m : matchesOrderedByDate) {
            TeamAccumulator home = byTeam.computeIfAbsent(
                    m.homeTeamId(), id -> new TeamAccumulator(id, m.homeTeamName(), m.homeTeamLogoUrl()));
            TeamAccumulator away = byTeam.computeIfAbsent(
                    m.awayTeamId(), id -> new TeamAccumulator(id, m.awayTeamName(), m.awayTeamLogoUrl()));

            home.played++;
            away.played++;
            home.goalsFor += m.homeGoals();
            home.goalsAgainst += m.awayGoals();
            away.goalsFor += m.awayGoals();
            away.goalsAgainst += m.homeGoals();

            if (m.homeGoals() > m.awayGoals()) {
                home.won++;
                away.lost++;
                home.formChronological.add(FormLetter.V);
                away.formChronological.add(FormLetter.P);
            } else if (m.homeGoals() < m.awayGoals()) {
                home.lost++;
                away.won++;
                home.formChronological.add(FormLetter.P);
                away.formChronological.add(FormLetter.V);
            } else {
                home.drawn++;
                away.drawn++;
                home.formChronological.add(FormLetter.E);
                away.formChronological.add(FormLetter.E);
            }
        }

        Comparator<TeamAccumulator> byStandingStats = Comparator
                .comparingInt(TeamAccumulator::points).reversed()
                .thenComparing(Comparator.comparingInt(TeamAccumulator::goalDifference).reversed())
                .thenComparing(Comparator.comparingInt((TeamAccumulator t) -> t.goalsFor).reversed());

        Comparator<TeamAccumulator> displayOrder = byStandingStats
                .thenComparing((TeamAccumulator t) -> t.teamName, String.CASE_INSENSITIVE_ORDER);

        List<TeamAccumulator> sorted = new ArrayList<>(byTeam.values());
        sorted.sort(displayOrder);

        List<StandingRow> rows = new ArrayList<>(sorted.size());
        int rank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            TeamAccumulator t = sorted.get(i);
            if (i > 0 && byStandingStats.compare(sorted.get(i - 1), t) != 0) {
                rank = i + 1;
            }
            int gd = t.goalsFor - t.goalsAgainst;
            rows.add(new StandingRow(
                    rank,
                    t.teamId,
                    t.teamName,
                    t.teamLogoUrl,
                    t.played,
                    t.won,
                    t.drawn,
                    t.lost,
                    t.goalsFor,
                    t.goalsAgainst,
                    gd,
                    t.points(),
                    recentFormMostRecentFirst(t.formChronological)
            ));
        }
        return rows;
    }

    private static final int RECENT_FORM_SIZE = 5;

    private static List<FormLetter> recentFormMostRecentFirst(List<FormLetter> chronological) {
        int n = chronological.size();
        if (n == 0) {
            return List.of();
        }
        int from = Math.max(0, n - RECENT_FORM_SIZE);
        List<FormLetter> last = chronological.subList(from, n);
        List<FormLetter> out = new ArrayList<>(last.size());
        for (int i = last.size() - 1; i >= 0; i--) {
            out.add(last.get(i));
        }
        return List.copyOf(out);
    }

    private static final class TeamAccumulator {
        private final int teamId;
        private final String teamName;
        private final String teamLogoUrl;
        private int played;
        private int won;
        private int drawn;
        private int lost;
        private int goalsFor;
        private int goalsAgainst;
        private final List<FormLetter> formChronological = new ArrayList<>();

        private TeamAccumulator(int teamId, String teamName, String teamLogoUrl) {
            this.teamId = teamId;
            this.teamName = teamName;
            this.teamLogoUrl = teamLogoUrl;
        }

        private int points() {
            return won * 3 + drawn;
        }

        private int goalDifference() {
            return goalsFor - goalsAgainst;
        }
    }
}
