package rndbet.rndbetpredictionsbackend.stattargets.domain;

import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;

import java.util.List;

/** Lee agregados de {@link TeamMatchStatsEntity} según ámbito y métrica (sin posesión). */
public final class StatTargetCurrentValues {

    private StatTargetCurrentValues() {}

    public static int resolve(
            StatMetric metric,
            TargetScope scope,
            int homeTeamId,
            int awayTeamId,
            List<TeamMatchStatsEntity> rows) {
        TeamMatchStatsEntity home = findByTeamId(rows, homeTeamId);
        TeamMatchStatsEntity away = findByTeamId(rows, awayTeamId);
        return switch (scope) {
            case GLOBAL -> value(home, metric) + value(away, metric);
            case HOME -> value(home, metric);
            case AWAY -> value(away, metric);
        };
    }

    private static TeamMatchStatsEntity findByTeamId(List<TeamMatchStatsEntity> rows, int teamId) {
        return rows.stream().filter(r -> r.getTeamId() != null && r.getTeamId() == teamId).findFirst().orElse(null);
    }

    private static int value(TeamMatchStatsEntity row, StatMetric metric) {
        if (row == null) {
            return 0;
        }
        return switch (metric) {
            case GOALS -> nz(row.getGoals());
            case SHOTS -> nz(row.getShots());
            case SHOTS_ON_TARGET -> nz(row.getShotsOnTarget());
            case SAVES -> nz(row.getSaves());
            case YELLOW_CARDS -> nz(row.getYellowCards());
            case RED_CARDS -> nz(row.getRedCards());
            case CORNERS -> nz(row.getCorners());
            case FOULS -> nz(row.getFouls());
            case OFFSIDES -> nz(row.getOffsides());
        };
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }
}
