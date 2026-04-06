package rndbet.rndbetpredictionsbackend.matchday.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.matchday.application.port.in.GetMatchdayFixturesUseCase;
import rndbet.rndbetpredictionsbackend.matchday.domain.CurrentMatchday;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competitions/{competitionId}/seasons/{seasonId}")
@RequiredArgsConstructor
public class MatchdayController {

    private final GetMatchdayFixturesUseCase getMatchdayFixturesUseCase;

    /**
     * Jornada “en curso”: primera jornada (por número) con partidos pendientes si las anteriores están cerradas.
     * Si la temporada está totalmente cerrada, devuelve la última jornada con {@code temporada_completada=true}.
     */
    @GetMapping("/rounds/current/matches")
    public MatchdayFixturesResponseDto currentRoundMatches(
            @PathVariable("competitionId") int competitionId, @PathVariable("seasonId") int seasonId) {
        CurrentMatchday current = getMatchdayFixturesUseCase.getCurrentFixtures(competitionId, seasonId);
        List<MatchdayFixtureLineDto> lines = current.partidos().stream().map(MatchdayFixtureLineDto::from).toList();
        Integer jornada = current.jornada();
        Boolean temporadaCompletada = jornada == null ? null : current.temporadaCompletada();
        return new MatchdayFixturesResponseDto(jornada, temporadaCompletada, lines);
    }

    @GetMapping("/rounds/{round}/matches")
    public MatchdayFixturesResponseDto matchesByRound(
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId,
            @PathVariable("round") int round) {
        List<MatchdayFixture> fixtures = getMatchdayFixturesUseCase.getFixtures(competitionId, seasonId, round);
        return new MatchdayFixturesResponseDto(
                round,
                null,
                fixtures.stream().map(MatchdayFixtureLineDto::from).toList());
    }
}
