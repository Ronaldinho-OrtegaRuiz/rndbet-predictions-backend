package rndbet.rndbetpredictionsbackend.stattargets.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.out.UserMatchStatTargetsPort;
import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;
import rndbet.rndbetpredictionsbackend.stattargets.domain.StatMetric;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetScope;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MatchStatTargetsPersistenceAdapter implements UserMatchStatTargetsPort {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<MatchStatTarget> ROW_MAPPER = MatchStatTargetsPersistenceAdapter::mapRow;

    private static MatchStatTarget mapRow(ResultSet rs, int rowNum) throws SQLException {
        String statStr = rs.getString("stat");
        StatMetric stat = StatMetric.fromApiValue(statStr)
                .orElseThrow(() -> new IllegalStateException("stat desconocido en BD: " + statStr));
        String scopeRaw = rs.getString("scope");
        TargetScope scope = TargetScope.fromApiValue(scopeRaw)
                .orElseThrow(() -> new IllegalStateException("scope desconocido en BD: " + scopeRaw));
        String stateRaw = rs.getString("state");
        TargetState state = TargetState.fromDb(stateRaw)
                .orElseThrow(() -> new IllegalStateException("state desconocido en BD: " + stateRaw));
        return new MatchStatTarget(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getInt("match_id"),
                scope,
                stat,
                rs.getInt("threshold"),
                state,
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class),
                rs.getObject("fulfilled_at", OffsetDateTime.class),
                rs.getObject("fulfilled_match_minute") != null
                        ? rs.getInt("fulfilled_match_minute")
                        : null,
                rs.getObject("failed_at", OffsetDateTime.class));
    }

    @Override
    public List<MatchStatTarget> listByUserAndMatch(long userId, int matchId) {
        return jdbcTemplate.query(
                """
                        SELECT id, user_id, match_id, scope, stat, threshold, state,
                               created_at, updated_at, fulfilled_at, fulfilled_match_minute, failed_at
                        FROM user_match_stat_targets
                        WHERE user_id = ? AND match_id = ?
                        ORDER BY id ASC
                        """,
                ROW_MAPPER,
                userId,
                matchId);
    }

    @Override
    public Optional<MatchStatTarget> findByIdAndUser(long targetId, long userId) {
        List<MatchStatTarget> rows = jdbcTemplate.query(
                """
                        SELECT id, user_id, match_id, scope, stat, threshold, state,
                               created_at, updated_at, fulfilled_at, fulfilled_match_minute, failed_at
                        FROM user_match_stat_targets
                        WHERE id = ? AND user_id = ?
                        """,
                ROW_MAPPER,
                targetId,
                userId);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        if (rows.size() > 1) {
            throw new IllegalStateException("Más de un objetivo con el mismo id y usuario.");
        }
        return Optional.of(rows.get(0));
    }

    @Override
    public long insert(long userId, int matchId, TargetScope scope, StatMetric stat, int threshold) {
        Long id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO user_match_stat_targets (user_id, match_id, scope, stat, threshold)
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                userId,
                matchId,
                scope.name(),
                stat.apiValue(),
                threshold);
        if (id == null) {
            throw new IllegalStateException("INSERT sin id devuelto.");
        }
        return id;
    }

    @Override
    public boolean updateThresholdIfPending(long targetId, long userId, int matchId, int newThreshold) {
        int n = jdbcTemplate.update(
                """
                        UPDATE user_match_stat_targets
                        SET threshold = ?, updated_at = NOW()
                        WHERE id = ? AND user_id = ? AND match_id = ? AND state = 'PENDING'
                        """,
                newThreshold,
                targetId,
                userId,
                matchId);
        return n > 0;
    }

    @Override
    public boolean deleteByIdUserAndMatch(long targetId, long userId, int matchId) {
        int n = jdbcTemplate.update(
                """
                        DELETE FROM user_match_stat_targets
                        WHERE id = ? AND user_id = ? AND match_id = ?
                        """,
                targetId,
                userId,
                matchId);
        return n > 0;
    }
}
