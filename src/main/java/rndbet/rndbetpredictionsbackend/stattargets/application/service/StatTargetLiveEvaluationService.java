package rndbet.rndbetpredictionsbackend.stattargets.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;
import rndbet.rndbetpredictionsbackend.jpa.repository.TeamMatchStatsRepository;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchProgressRules;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.out.UserMatchStatTargetsPort;
import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;
import rndbet.rndbetpredictionsbackend.stattargets.domain.StatTargetCurrentValues;

import java.util.List;

/**
 * Tras actualizar {@code team_match_stats} / estado del partido en vivo, ajusta filas
 * {@code user_match_stat_targets}: cumplimiento (≥ umbral) o fallo al cierre del partido.
 */
@Service
@Profile("!test")
@RequiredArgsConstructor
public class StatTargetLiveEvaluationService {

    private final UserMatchStatTargetsPort userMatchStatTargetsPort;
    private final TeamMatchStatsRepository teamMatchStatsRepository;

    public void evaluateAfterIngest(MatchEntity match) {
        int matchId = match.getId();
        int homeId = match.getHomeTeam().getId();
        int awayId = match.getAwayTeam().getId();
        List<TeamMatchStatsEntity> rows = teamMatchStatsRepository.findByMatchId(matchId);
        if (rows.isEmpty()) {
            applyFailuresIfTerminal(match);
            return;
        }

        List<MatchStatTarget> pending = userMatchStatTargetsPort.listPendingByMatchId(matchId);
        Integer minute = match.getCurrentMinute();
        for (MatchStatTarget t : pending) {
            int current = StatTargetCurrentValues.resolve(t.stat(), t.scope(), homeId, awayId, rows);
            if (current >= t.threshold()) {
                userMatchStatTargetsPort.markFulfilledIfPending(t.id(), minute);
            }
        }

        applyFailuresIfTerminal(match);
    }

    private void applyFailuresIfTerminal(MatchEntity match) {
        String status = match.getStatus();
        if (status == null || !MatchProgressRules.isTerminal(status)) {
            return;
        }
        for (MatchStatTarget t : userMatchStatTargetsPort.listPendingByMatchId(match.getId())) {
            userMatchStatTargetsPort.markFailedIfPending(t.id());
        }
    }
}
