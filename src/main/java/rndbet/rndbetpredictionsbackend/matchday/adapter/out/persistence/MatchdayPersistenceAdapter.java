package rndbet.rndbetpredictionsbackend.matchday.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import rndbet.rndbetpredictionsbackend.db.MatchRoundStatusRow;
import rndbet.rndbetpredictionsbackend.matchday.application.port.out.LoadMatchdayFixturesPort;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MatchdayPersistenceAdapter implements LoadMatchdayFixturesPort {

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
    public Map<Integer, List<String>> loadMatchStatusesByRound(int seasonId) {
        ResultSetExtractor<Map<Integer, List<String>>> extractor = rs -> {
            Map<Integer, List<String>> map = new HashMap<>();
            while (rs.next()) {
                MatchRoundStatusRow row = MatchRoundStatusRow.from(rs);
                if (row.round() == null) {
                    continue;
                }
                map.computeIfAbsent(row.round(), k -> new ArrayList<>()).add(row.status());
            }
            return map;
        };
        return jdbcTemplate.query(
                """
                        SELECT round, status FROM matches WHERE season_id = ?
                        """,
                extractor,
                seasonId);
    }

    @Override
    public List<MatchdayFixture> loadMatchesForRound(int seasonId, int round) {
        return jdbcTemplate.query(
                """
                        SELECT m.*,
                               ht.name AS home_team_name,
                               at.name AS away_team_name,
                               COALESCE(rc.red_home, 0) AS red_cards_home,
                               COALESCE(rc.red_away, 0) AS red_cards_away
                        FROM matches m
                        JOIN teams ht ON ht.id = m.home_team_id
                        JOIN teams at ON at.id = m.away_team_id
                        LEFT JOIN (
                            SELECT tms.match_id,
                                   SUM(CASE
                                           WHEN tms.team_id = mm.home_team_id
                                               THEN COALESCE(tms.red_cards, 0)
                                           ELSE 0
                                       END) AS red_home,
                                   SUM(CASE
                                           WHEN tms.team_id = mm.away_team_id
                                               THEN COALESCE(tms.red_cards, 0)
                                           ELSE 0
                                       END) AS red_away
                            FROM team_match_stats tms
                            JOIN matches mm ON mm.id = tms.match_id
                            GROUP BY tms.match_id
                        ) rc ON rc.match_id = m.id
                        WHERE m.season_id = ?
                          AND m.round = ?
                        ORDER BY m.date ASC NULLS LAST, m.id ASC
                        """,
                (rs, rowNum) -> MatchdayJdbcMapper.toFixture(rs),
                seasonId,
                round);
    }
}
