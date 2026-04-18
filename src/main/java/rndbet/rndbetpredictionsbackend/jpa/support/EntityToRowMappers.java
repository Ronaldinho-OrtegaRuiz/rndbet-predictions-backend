package rndbet.rndbetpredictionsbackend.jpa.support;

import rndbet.rndbetpredictionsbackend.db.MatchRow;
import rndbet.rndbetpredictionsbackend.db.TeamMatchStatsRow;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;

/** Convierte entidades JPA a los records JDBC existentes para reutilizar mappers de dominio. */
public final class EntityToRowMappers {

    private EntityToRowMappers() {}

    public static MatchRow toMatchRow(MatchEntity m) {
        return new MatchRow(
                m.getId(),
                m.getSeason().getId(),
                m.getDate(),
                m.getHomeTeam().getId(),
                m.getAwayTeam().getId(),
                m.getHomeScore(),
                m.getAwayScore(),
                m.getStatus(),
                m.getRound(),
                m.getStage(),
                m.getMatchGroup(),
                m.getCurrentMinute(),
                m.getAddedTime(),
                m.getLastUpdated());
    }

    public static TeamMatchStatsRow toTeamMatchStatsRow(TeamMatchStatsEntity e) {
        return new TeamMatchStatsRow(
                e.getId(),
                e.getMatchId(),
                e.getTeamId(),
                e.getIsHome(),
                e.getGoals(),
                e.getPossession(),
                e.getShots(),
                e.getShotsOnTarget(),
                e.getSaves(),
                e.getYellowCards(),
                e.getRedCards(),
                e.getCorners(),
                e.getFouls(),
                e.getOffsides());
    }
}
