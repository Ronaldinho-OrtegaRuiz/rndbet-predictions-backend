package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Fila de {@code public.players}. */
public record PlayerRow(Integer id, String name) {

    public static PlayerRow from(ResultSet rs) throws SQLException {
        return new PlayerRow(rs.getObject("id", Integer.class), rs.getString("name"));
    }
}
