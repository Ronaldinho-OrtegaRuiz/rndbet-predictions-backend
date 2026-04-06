package rndbet.rndbetpredictionsbackend.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Proyección mínima de {@code matches} para resolver jornada actual ({@code round} + {@code status}). */
public record MatchRoundStatusRow(Integer round, String status) {

    public static MatchRoundStatusRow from(ResultSet rs) throws SQLException {
        return new MatchRoundStatusRow(rs.getObject("round", Integer.class), rs.getString("status"));
    }
}
