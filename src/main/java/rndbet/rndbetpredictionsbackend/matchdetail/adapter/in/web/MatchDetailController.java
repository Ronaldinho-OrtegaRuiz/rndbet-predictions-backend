package rndbet.rndbetpredictionsbackend.matchdetail.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.in.GetMatchDetailUseCase;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;

@RestController
@RequestMapping("/api/v1/competitions/{competitionId}/seasons/{seasonId}")
@RequiredArgsConstructor
public class MatchDetailController {

    private final GetMatchDetailUseCase getMatchDetailUseCase;

    @GetMapping("/rounds/{round}/matches/{matchId}")
    public MatchDetailResponseDto matchDetail(
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId,
            @PathVariable("round") int round,
            @PathVariable("matchId") int matchId) {
        MatchDetail detail = getMatchDetailUseCase.getMatchDetail(competitionId, seasonId, round, matchId);
        return MatchDetailResponseDto.from(detail);
    }
}
