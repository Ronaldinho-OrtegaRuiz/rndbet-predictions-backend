package rndbet.rndbetpredictionsbackend.matchdetail.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import rndbet.rndbetpredictionsbackend.db.MatchRow;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchEventLine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class MatchDetailPersistenceAdapter implements LoadMatchDetailPort {

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
    public Optional<MatchDetail> loadMatchDetail(int competitionId, int seasonId, int round, int matchId) {
        Optional<MatchDetailHeaderBundle> header = loadHeader(competitionId, seasonId, round, matchId);
        if (header.isEmpty()) {
            return Optional.empty();
        }
        MatchDetailHeaderBundle h = header.get();
        MatchRow m = h.match();
        int homeTeamId = Objects.requireNonNull(m.homeTeamId(), "home_team_id");
        int awayTeamId = Objects.requireNonNull(m.awayTeamId(), "away_team_id");
        Map<String, Object> homeStats = new LinkedHashMap<>();
        Map<String, Object> awayStats = new LinkedHashMap<>();
        loadTeamStatsSplit(matchId, homeTeamId, awayTeamId, homeStats, awayStats);
        List<MatchEventLine> eventos = loadEvents(matchId, homeTeamId, awayTeamId);
        return Optional.of(MatchDetailJdbcMapper.toDetail(h, homeStats, awayStats, eventos));
    }

    private Optional<MatchDetailHeaderBundle> loadHeader(int competitionId, int seasonId, int round, int matchId) {
        ResultSetExtractor<Optional<MatchDetailHeaderBundle>> ext = MatchDetailJdbcMapper::firstHeaderOrEmpty;
        return jdbcTemplate.query(
                """
                        SELECT m.*,
                               ht.name AS home_team_name,
                               at.name AS away_team_name,
                               ht.logo_url AS home_team_logo_url,
                               at.logo_url AS away_team_logo_url
                        FROM matches m
                        JOIN teams ht ON ht.id = m.home_team_id
                        JOIN teams at ON at.id = m.away_team_id
                        JOIN seasons s ON s.id = m.season_id
                        WHERE m.id = ?
                          AND m.season_id = ?
                          AND m.round = ?
                          AND s.competition_id = ?
                        """,
                ext,
                matchId,
                seasonId,
                round,
                competitionId);
    }

    private void loadTeamStatsSplit(
            int matchId, int homeTeamId, int awayTeamId, Map<String, Object> homeOut, Map<String, Object> awayOut) {
        jdbcTemplate.query(
                """
                        SELECT * FROM team_match_stats WHERE match_id = ?
                        """,
                (RowCallbackHandler)
                        rs -> MatchDetailJdbcMapper.applyTeamStatRow(rs, homeTeamId, awayTeamId, homeOut, awayOut),
                matchId);
    }

    private List<MatchEventLine> loadEvents(int matchId, int homeTeamId, int awayTeamId) {
        return jdbcTemplate.query(
                """
                        SELECT me.*,
                               p.name AS player_name
                        FROM match_events me
                        LEFT JOIN players p ON p.id = me.player_id
                        WHERE me.match_id = ?
                        ORDER BY me.minute ASC NULLS LAST, me.id ASC
                        """,
                (rs, rowNum) -> MatchDetailJdbcMapper.toEventLine(rs, homeTeamId, awayTeamId),
                matchId);
    }
}
