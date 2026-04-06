package rndbet.rndbetpredictionsbackend.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/** Fila de {@code public.predictions}. */
public record PredictionRow(
        Integer id,
        Integer matchId,
        OffsetDateTime createdAt,
        BigDecimal expectedHomeGoals,
        BigDecimal expectedAwayGoals,
        BigDecimal probHomeWin,
        BigDecimal probDraw,
        BigDecimal probAwayWin,
        Integer predictedShots,
        Integer predictedShotsOnTarget,
        Integer predictedSaves,
        Integer predictedYellowCards,
        Integer predictedRedCards,
        Integer predictedCorners,
        Integer predictedFouls,
        Integer predictedOffsides) {

    public static PredictionRow from(ResultSet rs) throws SQLException {
        return new PredictionRow(
                rs.getObject("id", Integer.class),
                rs.getObject("match_id", Integer.class),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("expected_home_goals", BigDecimal.class),
                rs.getObject("expected_away_goals", BigDecimal.class),
                rs.getObject("prob_home_win", BigDecimal.class),
                rs.getObject("prob_draw", BigDecimal.class),
                rs.getObject("prob_away_win", BigDecimal.class),
                rs.getObject("predicted_shots", Integer.class),
                rs.getObject("predicted_shots_on_target", Integer.class),
                rs.getObject("predicted_saves", Integer.class),
                rs.getObject("predicted_yellow_cards", Integer.class),
                rs.getObject("predicted_red_cards", Integer.class),
                rs.getObject("predicted_corners", Integer.class),
                rs.getObject("predicted_fouls", Integer.class),
                rs.getObject("predicted_offsides", Integer.class));
    }
}
