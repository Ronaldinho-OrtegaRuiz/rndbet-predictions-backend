package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Fila de {@code public.seasons}. */
public record SeasonRow(Integer id, Integer competitionId, String year) {

    public static SeasonRow from(ResultSet rs) throws SQLException {
        return new SeasonRow(
                rs.getObject("id", Integer.class),
                rs.getObject("competition_id", Integer.class),
                rs.getString("year"));
    }
}
