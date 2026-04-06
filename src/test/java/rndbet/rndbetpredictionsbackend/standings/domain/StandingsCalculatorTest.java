package rndbet.rndbetpredictionsbackend.standings.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StandingsCalculatorTest {

    @Test
    void threeTeamsRoundRobin_pointsAndRank() {
        Instant t0 = Instant.parse("2024-08-01T12:00:00Z");
        Instant t1 = Instant.parse("2024-08-08T12:00:00Z");
        Instant t2 = Instant.parse("2024-08-15T12:00:00Z");

        List<FinishedMatchForStandings> matches = List.of(
                new FinishedMatchForStandings(1, t0, 1, "A", null, 2, "B", null, 2, 0),
                new FinishedMatchForStandings(2, t1, 2, "B", null, 3, "C", null, 1, 1),
                new FinishedMatchForStandings(3, t2, 3, "C", null, 1, "A", null, 0, 3));

        List<StandingRow> table = StandingsCalculator.compute(matches);

        assertThat(table).hasSize(3);

        StandingRow a = table.stream().filter(r -> r.teamId() == 1).findFirst().orElseThrow();
        assertThat(a.won()).isEqualTo(2);
        assertThat(a.drawn()).isEqualTo(0);
        assertThat(a.lost()).isEqualTo(0);
        assertThat(a.points()).isEqualTo(6);
        assertThat(a.goalsFor()).isEqualTo(5);
        assertThat(a.goalsAgainst()).isEqualTo(0);
        assertThat(a.form()).containsExactly(FormLetter.V, FormLetter.V);

        StandingRow b = table.stream().filter(r -> r.teamId() == 2).findFirst().orElseThrow();
        assertThat(b.points()).isEqualTo(1);

        StandingRow c = table.stream().filter(r -> r.teamId() == 3).findFirst().orElseThrow();
        assertThat(c.points()).isEqualTo(1);

        assertThat(table.get(0).teamId()).isEqualTo(1);
    }

    @Test
    void tieBreakers_samePoints_goalDifferenceThenGoalsFor() {
        Instant t0 = Instant.parse("2024-08-01T12:00:00Z");
        Instant t1 = Instant.parse("2024-08-02T12:00:00Z");

        List<FinishedMatchForStandings> matches = List.of(
                new FinishedMatchForStandings(1, t0, 1, "BetterGd", null, 3, "Z", null, 3, 0),
                new FinishedMatchForStandings(2, t1, 2, "WorseGd", null, 3, "Z", null, 2, 0));

        List<StandingRow> table = StandingsCalculator.compute(matches);

        assertThat(table.get(0).teamName()).isEqualTo("BetterGd");
        assertThat(table.get(1).teamName()).isEqualTo("WorseGd");
        assertThat(table.get(2).teamName()).isEqualTo("Z");
    }

    @Test
    void tieBreakers_samePointsAndGd_moreGoalsForWins() {
        Instant t0 = Instant.parse("2024-08-01T12:00:00Z");
        Instant t1 = Instant.parse("2024-08-02T12:00:00Z");

        List<FinishedMatchForStandings> matches = List.of(
                new FinishedMatchForStandings(1, t0, 1, "LowerGf", null, 3, "C", null, 2, 1),
                new FinishedMatchForStandings(2, t1, 2, "HigherGf", null, 3, "C", null, 3, 2));

        List<StandingRow> table = StandingsCalculator.compute(matches);

        assertThat(table.get(0).teamName()).isEqualTo("HigherGf");
        assertThat(table.get(1).teamName()).isEqualTo("LowerGf");
    }

    @Test
    void formKeepsOnlyFiveMostRecent() {
        Instant base = Instant.parse("2024-08-01T12:00:00Z");
        List<FinishedMatchForStandings> matches = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Instant when = base.plusSeconds(i * 86_400L);
            matches.add(new FinishedMatchForStandings(i + 1, when, 1, "Solo", null, 2, "Opp", null, 1, 0));
        }
        List<StandingRow> table = StandingsCalculator.compute(matches);
        StandingRow solo = table.stream().filter(r -> r.teamId() == 1).findFirst().orElseThrow();
        assertThat(solo.form()).hasSize(5);
        assertThat(solo.form()).containsExactly(
                FormLetter.V, FormLetter.V, FormLetter.V, FormLetter.V, FormLetter.V);
    }

    @Test
    void sharedPosition_twoTeamsTied() {
        Instant t0 = Instant.parse("2024-08-01T12:00:00Z");
        List<FinishedMatchForStandings> matches = List.of(
                new FinishedMatchForStandings(1, t0, 1, "A", null, 2, "B", null, 1, 1));

        List<StandingRow> table = StandingsCalculator.compute(matches);

        assertThat(table).hasSize(2);
        assertThat(table.get(0).position()).isEqualTo(1);
        assertThat(table.get(1).position()).isEqualTo(1);
    }
}
