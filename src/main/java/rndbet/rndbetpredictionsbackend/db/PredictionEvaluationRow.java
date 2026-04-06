package rndbet.rndbetpredictionsbackend.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Fila de {@code public.prediction_evaluations}. */
public record PredictionEvaluationRow(
        Integer id,
        Integer predictionId,
        Integer actualHomeGoals,
        Integer actualAwayGoals,
        Integer actualShots,
        Integer actualShotsOnTarget,
        Integer actualSaves,
        Integer actualYellowCards,
        Integer actualRedCards,
        Integer actualCorners,
        Integer actualFouls,
        Integer actualOffsides,
        BigDecimal errorGoals,
        BigDecimal errorShots,
        BigDecimal errorCorners,
        BigDecimal errorCards,
        Boolean correctResult) {

    public static PredictionEvaluationRow from(ResultSet rs) throws SQLException {
        return new PredictionEvaluationRow(
                rs.getObject("id", Integer.class),
                rs.getObject("prediction_id", Integer.class),
                rs.getObject("actual_home_goals", Integer.class),
                rs.getObject("actual_away_goals", Integer.class),
                rs.getObject("actual_shots", Integer.class),
                rs.getObject("actual_shots_on_target", Integer.class),
                rs.getObject("actual_saves", Integer.class),
                rs.getObject("actual_yellow_cards", Integer.class),
                rs.getObject("actual_red_cards", Integer.class),
                rs.getObject("actual_corners", Integer.class),
                rs.getObject("actual_fouls", Integer.class),
                rs.getObject("actual_offsides", Integer.class),
                rs.getObject("error_goals", BigDecimal.class),
                rs.getObject("error_shots", BigDecimal.class),
                rs.getObject("error_corners", BigDecimal.class),
                rs.getObject("error_cards", BigDecimal.class),
                rs.getObject("correct_result", Boolean.class));
    }
}
