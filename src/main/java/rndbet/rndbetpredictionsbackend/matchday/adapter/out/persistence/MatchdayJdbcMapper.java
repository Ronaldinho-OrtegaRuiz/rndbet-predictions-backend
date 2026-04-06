package rndbet.rndbetpredictionsbackend.matchday.adapter.out.persistence;

import rndbet.rndbetpredictionsbackend.db.MatchRow;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

final class MatchdayJdbcMapper {

    private MatchdayJdbcMapper() {}

    /**
     * {@code ResultSet} con {@code m.*}, nombres y logos de equipos,
     * {@code red_cards_home}, {@code red_cards_away} (como en la query de jornada).
     */
    static MatchdayFixture toFixture(ResultSet rs) throws SQLException {
        MatchRow m = MatchRow.from(rs);
        Instant when = m.date() != null ? m.date().toInstant() : null;
        return new MatchdayFixture(
                Objects.requireNonNull(m.id()),
                when,
                m.status(),
                rs.getString("home_team_name"),
                rs.getString("home_team_logo_url"),
                m.homeScore(),
                rs.getString("away_team_name"),
                rs.getString("away_team_logo_url"),
                m.awayScore(),
                rs.getInt("red_cards_home"),
                rs.getInt("red_cards_away"));
    }
}
