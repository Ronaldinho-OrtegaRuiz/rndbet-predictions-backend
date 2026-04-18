package rndbet.rndbetpredictionsbackend.stattargets.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.security.JwtAuthentication;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.in.MatchStatTargetsUseCase;
import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competitions/{competitionId}/seasons/{seasonId}/rounds/{round}/matches/{matchId}")
@RequiredArgsConstructor
public class MatchStatTargetsController {

    private final MatchStatTargetsUseCase matchStatTargetsUseCase;

    @GetMapping("/stat-targets")
    public List<MatchStatTargetResponseDto> list(
            Authentication authentication,
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId,
            @PathVariable("round") int round,
            @PathVariable("matchId") int matchId) {
        long userId = userId(authentication);
        List<MatchStatTarget> items =
                matchStatTargetsUseCase.list(userId, competitionId, seasonId, round, matchId);
        return items.stream().map(MatchStatTargetResponseDto::from).toList();
    }

    @PostMapping("/stat-targets")
    @ResponseStatus(HttpStatus.CREATED)
    public MatchStatTargetResponseDto create(
            Authentication authentication,
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId,
            @PathVariable("round") int round,
            @PathVariable("matchId") int matchId,
            @RequestBody CreateMatchStatTargetRequestDto body) {
        long userId = userId(authentication);
        MatchStatTarget created = matchStatTargetsUseCase.create(
                userId,
                competitionId,
                seasonId,
                round,
                matchId,
                body.estadistica(),
                body.ambito(),
                body.umbral());
        return MatchStatTargetResponseDto.from(created);
    }

    @PatchMapping("/stat-targets/{targetId}")
    public MatchStatTargetResponseDto updateThreshold(
            Authentication authentication,
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId,
            @PathVariable("round") int round,
            @PathVariable("matchId") int matchId,
            @PathVariable("targetId") long targetId,
            @RequestBody UpdateMatchStatTargetRequestDto body) {
        long userId = userId(authentication);
        MatchStatTarget updated = matchStatTargetsUseCase.updateThreshold(
                userId, competitionId, seasonId, round, matchId, targetId, body.umbral());
        return MatchStatTargetResponseDto.from(updated);
    }

    @DeleteMapping("/stat-targets/{targetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            Authentication authentication,
            @PathVariable("competitionId") int competitionId,
            @PathVariable("seasonId") int seasonId,
            @PathVariable("round") int round,
            @PathVariable("matchId") int matchId,
            @PathVariable("targetId") long targetId) {
        long userId = userId(authentication);
        matchStatTargetsUseCase.delete(userId, competitionId, seasonId, round, matchId, targetId);
    }

    private static long userId(Authentication authentication) {
        if (authentication instanceof JwtAuthentication jwt) {
            return jwt.getUserId();
        }
        throw new IllegalStateException("Autenticación JWT esperada");
    }
}
