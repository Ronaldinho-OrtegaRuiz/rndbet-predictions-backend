package rndbet.rndbetpredictionsbackend.standings.adapter.out.persistence;

import rndbet.rndbetpredictionsbackend.db.MatchRow;
import rndbet.rndbetpredictionsbackend.standings.domain.FinishedMatchForStandings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

final class StandingsJdbcMapper {

    private StandingsJdbcMapper() {}

    /**
     * {@code ResultSet} con {@code m.*}, nombres y {@code logo_url} de local / visitante (query de partidos
     * finalizados de la temporada).
     */
    static FinishedMatchForStandings toFinishedMatch(ResultSet rs) throws SQLException {
        MatchRow m = MatchRow.from(rs);
        Instant when = m.date() != null ? m.date().toInstant() : Instant.EPOCH;
        return new FinishedMatchForStandings(
                Objects.requireNonNull(m.id()),
                when,
                Objects.requireNonNull(m.homeTeamId()),
                rs.getString("home_team_name"),
                rs.getString("home_team_logo_url"),
                Objects.requireNonNull(m.awayTeamId()),
                rs.getString("away_team_name"),
                rs.getString("away_team_logo_url"),
                Objects.requireNonNull(m.homeScore()),
                Objects.requireNonNull(m.awayScore()));
    }
}
