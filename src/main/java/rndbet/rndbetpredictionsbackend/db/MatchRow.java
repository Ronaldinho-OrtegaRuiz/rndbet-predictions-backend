package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 * Fila de {@code public.matches}. La columna SQL {@code "group"} se expone como {@code matchGroup}.
 */
public record MatchRow(
        Integer id,
        Integer seasonId,
        OffsetDateTime date,
        Integer homeTeamId,
        Integer awayTeamId,
        Integer homeScore,
        Integer awayScore,
        String status,
        Integer round,
        String stage,
        String matchGroup,
        Integer currentMinute,
        Integer addedTime,
        OffsetDateTime lastUpdated) {

    public static MatchRow from(ResultSet rs) throws SQLException {
        return new MatchRow(
                rs.getObject("id", Integer.class),
                rs.getObject("season_id", Integer.class),
                rs.getObject("date", OffsetDateTime.class),
                rs.getObject("home_team_id", Integer.class),
                rs.getObject("away_team_id", Integer.class),
                rs.getObject("home_score", Integer.class),
                rs.getObject("away_score", Integer.class),
                rs.getString("status"),
                rs.getObject("round", Integer.class),
                rs.getString("stage"),
                rs.getString("group"),
                rs.getObject("current_minute", Integer.class),
                rs.getObject("added_time", Integer.class),
                rs.getObject("last_updated", OffsetDateTime.class));
    }
}
