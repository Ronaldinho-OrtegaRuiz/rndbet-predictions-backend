package rndbet.rndbetpredictionsbackend.livetrack;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Selección nativa de partidos del “día” (rango en instantes) aún no marcados como enviados al scraper.
 */
@Component
@Profile("!test")
public class LiveTrackDayMatchSelection {

    private static final String SQL =
            """
                    SELECT m.id, m.date, m.round, m.stage, m."group", c.name, ht.name, at.name
                    FROM matches m
                    JOIN seasons s ON s.id = m.season_id
                    JOIN competitions c ON c.id = s.competition_id
                    JOIN teams ht ON ht.id = m.home_team_id
                    JOIN teams at ON at.id = m.away_team_id
                    WHERE COALESCE(m.live_track_enqueued, false) = false
                      AND m.date >= :start AND m.date < :end
                    ORDER BY m.date ASC NULLS LAST, m.id ASC
                    """;

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<LiveTrackMatchOutbound> findPendingBetween(OffsetDateTime startInclusive, OffsetDateTime endExclusive) {
        List<?> raw = entityManager
                .createNativeQuery(SQL)
                .setParameter("start", startInclusive)
                .setParameter("end", endExclusive)
                .getResultList();
        List<LiveTrackMatchOutbound> out = new ArrayList<>();
        for (Object rowObj : raw) {
            Object[] row = (Object[]) rowObj;
            int matchId = ((Number) row[0]).intValue();
            OffsetDateTime fecha = toOffsetDateTime(row[1]);
            Integer jornada = row[2] == null ? null : ((Number) row[2]).intValue();
            String fase = row[3] != null ? row[3].toString() : null;
            String grupo = row[4] != null ? row[4].toString() : null;
            String competicion = row[5] != null ? row[5].toString() : null;
            String equipoLocal = row[6] != null ? row[6].toString() : null;
            String equipoVisitante = row[7] != null ? row[7].toString() : null;
            out.add(new LiveTrackMatchOutbound(
                    matchId, fecha, jornada, fase, grupo, competicion, equipoLocal, equipoVisitante));
        }
        return out;
    }

    private static OffsetDateTime toOffsetDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof OffsetDateTime odt) {
            return odt;
        }
        if (value instanceof Timestamp ts) {
            return ts.toInstant().atOffset(ZoneOffset.UTC);
        }
        throw new IllegalStateException("Tipo de fecha no soportado: " + value.getClass());
    }
}
