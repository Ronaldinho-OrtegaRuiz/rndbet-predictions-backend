package rndbet.rndbetpredictionsbackend.matchday.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentMatchdayResolverTest {

    @Test
    void emptySeason() {
        CurrentMatchdayResolver.Resolution r = CurrentMatchdayResolver.resolve(Map.of());
        assertThat(r.round()).isNull();
        assertThat(r.seasonCompleted()).isFalse();
    }

    @Test
    void firstRoundInProgressLaterRoundsExist() {
        Map<Integer, List<String>> m =
                Map.of(1, List.of("finished", "schedule"), 2, List.of("schedule", "schedule"));
        CurrentMatchdayResolver.Resolution r = CurrentMatchdayResolver.resolve(m);
        assertThat(r.round()).isEqualTo(1);
        assertThat(r.seasonCompleted()).isFalse();
    }

    @Test
    void previousClosedCurrentMixed() {
        Map<Integer, List<String>> m = Map.of(
                1,
                List.of("finished", "finished", "suspended"),
                2,
                List.of("finished", "schedule", "schedule"),
                3,
                List.of("schedule"));
        CurrentMatchdayResolver.Resolution r = CurrentMatchdayResolver.resolve(m);
        assertThat(r.round()).isEqualTo(2);
        assertThat(r.seasonCompleted()).isFalse();
    }

    @Test
    void allRoundsTerminal_returnsLastRound() {
        Map<Integer, List<String>> m =
                Map.of(1, List.of("finished", "finished"), 2, List.of("suspended", "cancelled"));
        CurrentMatchdayResolver.Resolution r = CurrentMatchdayResolver.resolve(m);
        assertThat(r.round()).isEqualTo(2);
        assertThat(r.seasonCompleted()).isTrue();
    }

    @Test
    void scheduleInEarlierRoundBlocksLaterCurrent() {
        Map<Integer, List<String>> m =
                Map.of(1, List.of("finished", "schedule"), 2, List.of("schedule"));
        CurrentMatchdayResolver.Resolution r = CurrentMatchdayResolver.resolve(m);
        assertThat(r.round()).isEqualTo(1);
        assertThat(r.seasonCompleted()).isFalse();
    }

    @Test
    void statusComparisonIsCaseInsensitive() {
        assertThat(MatchProgressRules.isTerminal("Finished")).isTrue();
        assertThat(MatchProgressRules.isTerminal("SCHEDULE")).isFalse();
    }
}
