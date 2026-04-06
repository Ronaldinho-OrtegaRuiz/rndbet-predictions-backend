package rndbet.rndbetpredictionsbackend.standings.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.standings.application.port.out.LoadStandingsDataPort;
import rndbet.rndbetpredictionsbackend.standings.domain.FinishedMatchForStandings;

import java.util.List;

@RequiredArgsConstructor
public class StandingsPersistenceAdapter implements LoadStandingsDataPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean seasonBelongsToCompetition(int competitionId, int seasonId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM seasons WHERE id = ? AND competition_id = ?
                        """,
                Integer.class,
                seasonId,
                competitionId);
        return count != null && count > 0;
    }

    @Override
    public List<FinishedMatchForStandings> loadFinishedMatchesBySeason(int seasonId) {
        return jdbcTemplate.query(
                """
                        SELECT m.*,
                               th.name AS home_team_name,
                               ta.name AS away_team_name
                        FROM matches m
                        JOIN teams th ON th.id = m.home_team_id
                        JOIN teams ta ON ta.id = m.away_team_id
                        WHERE m.season_id = ?
                          AND m.home_score IS NOT NULL
                          AND m.away_score IS NOT NULL
                        ORDER BY m.date ASC NULLS LAST, m.id ASC
                        """,
                (rs, rowNum) -> StandingsJdbcMapper.toFinishedMatch(rs),
                seasonId);
    }
}
