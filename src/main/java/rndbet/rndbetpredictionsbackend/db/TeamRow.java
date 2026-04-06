package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Fila de {@code public.teams}. */
public record TeamRow(Integer id, String name, String country) {

    public static TeamRow from(ResultSet rs) throws SQLException {
        return new TeamRow(
                rs.getObject("id", Integer.class),
                rs.getString("name"),
                rs.getString("country"));
    }
}
