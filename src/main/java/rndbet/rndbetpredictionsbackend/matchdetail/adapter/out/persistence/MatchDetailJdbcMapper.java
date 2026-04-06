package rndbet.rndbetpredictionsbackend.matchdetail.adapter.out.persistence;

import rndbet.rndbetpredictionsbackend.db.MatchEventRow;
import rndbet.rndbetpredictionsbackend.db.MatchRow;
import rndbet.rndbetpredictionsbackend.db.TeamMatchStatsRow;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchEventLine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class MatchDetailJdbcMapper {

    private MatchDetailJdbcMapper() {}

    /**
     * Cabecera del detalle: {@code m.*} + {@code home_team_name}, {@code away_team_name}. Avanza el cursor con
     * {@link ResultSet#next()}; si no hay fila, devuelve vacío.
     */
    static Optional<MatchDetailHeaderBundle> firstHeaderOrEmpty(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return Optional.empty();
        }
        MatchRow m = MatchRow.from(rs);
        return Optional.of(new MatchDetailHeaderBundle(
                m, rs.getString("home_team_name"), rs.getString("away_team_name")));
    }

    /** Una fila de {@code team_match_stats} hacia los mapas local / visitante. */
    static void applyTeamStatRow(
            ResultSet rs,
            int homeTeamId,
            int awayTeamId,
            Map<String, Object> homeOut,
            Map<String, Object> awayOut)
            throws SQLException {
        TeamMatchStatsRow row = TeamMatchStatsRow.from(rs);
        Integer tid = row.teamId();
        if (tid == null) {
            return;
        }
        Map<String, Object> map = row.toStatisticsMap();
        if (tid == homeTeamId) {
            homeOut.putAll(map);
        } else if (tid == awayTeamId) {
            awayOut.putAll(map);
        }
    }

    /** Fila de eventos + nombre de jugador del join. */
    static MatchEventLine toEventLine(ResultSet rs, int homeTeamId, int awayTeamId) throws SQLException {
        MatchEventRow row = MatchEventRow.from(rs);
        String jugador = rs.getString("player_name");
        Integer tid = row.teamId();
        String lado = null;
        if (tid != null) {
            if (tid == homeTeamId) {
                lado = "local";
            } else if (tid == awayTeamId) {
                lado = "visitante";
            }
        }
        return new MatchEventLine(row.minute(), row.eventType(), jugador, lado);
    }

    static MatchDetail toDetail(
            MatchDetailHeaderBundle h,
            Map<String, Object> homeStats,
            Map<String, Object> awayStats,
            List<MatchEventLine> eventos) {
        MatchRow m = h.match();
        Instant fecha = m.date() != null ? m.date().toInstant() : null;
        return new MatchDetail(
                Objects.requireNonNull(m.id()),
                fecha,
                m.status(),
                h.homeTeamName(),
                h.awayTeamName(),
                m.homeScore(),
                m.awayScore(),
                homeStats,
                awayStats,
                eventos);
    }
}

/** Paquete-local: resultado de la query de cabecera del partido. */
record MatchDetailHeaderBundle(MatchRow match, String homeTeamName, String awayTeamName) {}
