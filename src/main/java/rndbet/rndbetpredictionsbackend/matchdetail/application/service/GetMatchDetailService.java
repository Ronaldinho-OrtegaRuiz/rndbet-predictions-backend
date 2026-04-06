package rndbet.rndbetpredictionsbackend.matchdetail.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rndbet.rndbetpredictionsbackend.matchdetail.application.exception.MatchNotFoundException;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.in.GetMatchDetailUseCase;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;
import rndbet.rndbetpredictionsbackend.standings.application.exception.SeasonNotFoundException;

@Service
@RequiredArgsConstructor
public class GetMatchDetailService implements GetMatchDetailUseCase {

    private final LoadMatchDetailPort loadMatchDetailPort;

    @Override
    public MatchDetail getMatchDetail(int competitionId, int seasonId, int round, int matchId) {
        if (!loadMatchDetailPort.seasonBelongsToCompetition(competitionId, seasonId)) {
            throw new SeasonNotFoundException(
                    "La temporada %d no existe o no pertenece a la competición %d.".formatted(seasonId, competitionId));
        }
        return loadMatchDetailPort
                .loadMatchDetail(competitionId, seasonId, round, matchId)
                .orElseThrow(() -> new MatchNotFoundException(
                        "El partido %d no existe o no coincide con la competición, temporada y jornada indicadas."
                                .formatted(matchId)));
    }
}
