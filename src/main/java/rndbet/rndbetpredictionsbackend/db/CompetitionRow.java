package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Fila de {@code public.competitions}. */
public record CompetitionRow(Integer id, String name, String type, String format) {

    public static CompetitionRow from(ResultSet rs) throws SQLException {
        return new CompetitionRow(
                rs.getObject("id", Integer.class),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("format"));
    }
}
