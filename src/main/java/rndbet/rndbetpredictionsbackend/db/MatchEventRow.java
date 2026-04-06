package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/** Fila de {@code public.match_events}. {@code extra_data} se expone como texto JSON. */
public record MatchEventRow(
        Integer id,
        Integer matchId,
        Integer teamId,
        Integer playerId,
        Integer minute,
        String eventType,
        String extraDataJson,
        OffsetDateTime createdAt) {

    public static MatchEventRow from(ResultSet rs) throws SQLException {
        Object extra = rs.getObject("extra_data");
        String extraJson = extra == null ? null : extra.toString();
        return new MatchEventRow(
                rs.getObject("id", Integer.class),
                rs.getObject("match_id", Integer.class),
                rs.getObject("team_id", Integer.class),
                rs.getObject("player_id", Integer.class),
                rs.getObject("minute", Integer.class),
                rs.getString("event_type"),
                extraJson,
                rs.getObject("created_at", OffsetDateTime.class));
    }
}
