package rndbet.rndbetpredictionsbackend.matchdetail.application.port.out;

import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;

import java.util.Optional;

public interface LoadMatchDetailPort {

    boolean seasonBelongsToCompetition(int competitionId, int seasonId);

    /**
     * Comprueba que el partido exista y coincida con competición, temporada y jornada, sin cargar
     * estadísticas ni eventos.
     */
    boolean matchExistsInSeasonRound(int competitionId, int seasonId, int round, int matchId);

    Optional<MatchDetail> loadMatchDetail(int competitionId, int seasonId, int round, int matchId);
}
