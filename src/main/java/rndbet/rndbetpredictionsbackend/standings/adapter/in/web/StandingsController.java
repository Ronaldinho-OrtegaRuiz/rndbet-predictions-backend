package rndbet.rndbetpredictionsbackend.standings.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.standings.application.port.in.GetStandingsUseCase;
import rndbet.rndbetpredictionsbackend.standings.domain.StandingRow;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competitions/{competitionId}/seasons/{seasonId}")
@RequiredArgsConstructor
public class StandingsController {

    private final GetStandingsUseCase getStandingsUseCase;

    @GetMapping("/standings")
    public StandingsResponseDto standings(
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId) {
        List<StandingRow> rows = getStandingsUseCase.getStandings(competitionId, seasonId);
        return new StandingsResponseDto(rows.stream().map(StandingLineDto::from).toList());
    }
}
