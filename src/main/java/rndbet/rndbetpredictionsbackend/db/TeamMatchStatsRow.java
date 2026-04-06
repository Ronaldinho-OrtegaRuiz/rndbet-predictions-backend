package rndbet.rndbetpredictionsbackend.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Fila de {@code public.team_match_stats}. */
public record TeamMatchStatsRow(
        Integer id,
        Integer matchId,
        Integer teamId,
        Boolean isHome,
        Integer goals,
        BigDecimal possession,
        Integer shots,
        Integer shotsOnTarget,
        Integer saves,
        Integer yellowCards,
        Integer redCards,
        Integer corners,
        Integer fouls,
        Integer offsides) {

    public static TeamMatchStatsRow from(ResultSet rs) throws SQLException {
        return new TeamMatchStatsRow(
                rs.getObject("id", Integer.class),
                rs.getObject("match_id", Integer.class),
                rs.getObject("team_id", Integer.class),
                rs.getObject("is_home", Boolean.class),
                rs.getObject("goals", Integer.class),
                rs.getObject("possession", BigDecimal.class),
                rs.getObject("shots", Integer.class),
                rs.getObject("shots_on_target", Integer.class),
                rs.getObject("saves", Integer.class),
                rs.getObject("yellow_cards", Integer.class),
                rs.getObject("red_cards", Integer.class),
                rs.getObject("corners", Integer.class),
                rs.getObject("fouls", Integer.class),
                rs.getObject("offsides", Integer.class));
    }

    /**
     * Campos para la API de detalle de partido: todo salvo claves y {@code goals} (el marcador va en {@code matches}).
     */
    public Map<String, Object> toStatisticsMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        if (isHome != null) {
            m.put("is_home", isHome);
        }
        if (possession != null) {
            m.put("possession", possession);
        }
        if (shots != null) {
            m.put("shots", shots);
        }
        if (shotsOnTarget != null) {
            m.put("shots_on_target", shotsOnTarget);
        }
        if (saves != null) {
            m.put("saves", saves);
        }
        if (yellowCards != null) {
            m.put("yellow_cards", yellowCards);
        }
        if (redCards != null) {
            m.put("red_cards", redCards);
        }
        if (corners != null) {
            m.put("corners", corners);
        }
        if (fouls != null) {
            m.put("fouls", fouls);
        }
        if (offsides != null) {
            m.put("offsides", offsides);
        }
        return m;
    }
}
