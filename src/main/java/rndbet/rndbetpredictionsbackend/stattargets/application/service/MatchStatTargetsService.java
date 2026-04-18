package rndbet.rndbetpredictionsbackend.stattargets.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rndbet.rndbetpredictionsbackend.matchdetail.application.exception.MatchNotFoundException;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;
import rndbet.rndbetpredictionsbackend.standings.application.exception.SeasonNotFoundException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.InvalidStatTargetException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.StatTargetDuplicateException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.StatTargetNotEditableException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.StatTargetNotFoundException;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.in.MatchStatTargetsUseCase;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.out.UserMatchStatTargetsPort;
import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;
import rndbet.rndbetpredictionsbackend.stattargets.domain.StatMetric;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetScope;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetState;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchStatTargetsService implements MatchStatTargetsUseCase {

    private final LoadMatchDetailPort loadMatchDetailPort;
    private final UserMatchStatTargetsPort userMatchStatTargetsPort;

    private void ensureMatchContext(int competitionId, int seasonId, int round, int matchId) {
        if (!loadMatchDetailPort.seasonBelongsToCompetition(competitionId, seasonId)) {
            throw new SeasonNotFoundException(
                    "La temporada %d no existe o no pertenece a la competición %d.".formatted(seasonId, competitionId));
        }
        if (!loadMatchDetailPort.matchExistsInSeasonRound(competitionId, seasonId, round, matchId)) {
            throw new MatchNotFoundException(
                    "El partido %d no existe o no coincide con la competición, temporada y jornada indicadas."
                            .formatted(matchId));
        }
    }

    private static void validateThreshold(int threshold) {
        if (threshold <= 0) {
            throw new InvalidStatTargetException("El umbral debe ser un entero mayor que cero.");
        }
    }

    @Override
    public List<MatchStatTarget> list(long userId, int competitionId, int seasonId, int round, int matchId) {
        ensureMatchContext(competitionId, seasonId, round, matchId);
        return userMatchStatTargetsPort.listByUserAndMatch(userId, matchId);
    }

    @Override
    @Transactional
    public MatchStatTarget create(
            long userId,
            int competitionId,
            int seasonId,
            int round,
            int matchId,
            String statApiValue,
            String scopeApiValue,
            int threshold) {
        ensureMatchContext(competitionId, seasonId, round, matchId);
        validateThreshold(threshold);
        StatMetric stat =
                StatMetric.fromApiValue(statApiValue).orElseThrow(() -> new InvalidStatTargetException(
                        "Estadística no válida o no permitida para objetivos."));
        TargetScope scope = TargetScope.fromApiValue(scopeApiValue)
                .orElseThrow(() -> new InvalidStatTargetException("Ámbito no válido: use GLOBAL, HOME o AWAY."));
        try {
            long id = userMatchStatTargetsPort.insert(userId, matchId, scope, stat, threshold);
            return userMatchStatTargetsPort
                    .findByIdAndUser(id, userId)
                    .orElseThrow(() -> new IllegalStateException("No se pudo recargar el objetivo creado."));
        } catch (DataIntegrityViolationException e) {
            throw new StatTargetDuplicateException(
                    "Ya existe un objetivo para esta estadística y ámbito en este partido.");
        }
    }

    @Override
    @Transactional
    public MatchStatTarget updateThreshold(
            long userId,
            int competitionId,
            int seasonId,
            int round,
            int matchId,
            long targetId,
            int newThreshold) {
        ensureMatchContext(competitionId, seasonId, round, matchId);
        validateThreshold(newThreshold);
        MatchStatTarget existing = userMatchStatTargetsPort
                .findByIdAndUser(targetId, userId)
                .orElseThrow(() -> new StatTargetNotFoundException("Objetivo no encontrado."));
        if (existing.matchId() != matchId) {
            throw new StatTargetNotFoundException("Objetivo no encontrado.");
        }
        if (existing.state() != TargetState.PENDING) {
            throw new StatTargetNotEditableException(
                    "Solo se puede editar el umbral mientras el objetivo está pendiente.");
        }
        boolean ok = userMatchStatTargetsPort.updateThresholdIfPending(targetId, userId, matchId, newThreshold);
        if (!ok) {
            throw new StatTargetNotEditableException(
                    "No se pudo actualizar el objetivo; puede haber cambiado de estado.");
        }
        return userMatchStatTargetsPort
                .findByIdAndUser(targetId, userId)
                .orElseThrow(() -> new IllegalStateException("No se pudo recargar el objetivo actualizado."));
    }

    @Override
    @Transactional
    public void delete(
            long userId, int competitionId, int seasonId, int round, int matchId, long targetId) {
        ensureMatchContext(competitionId, seasonId, round, matchId);
        boolean deleted = userMatchStatTargetsPort.deleteByIdUserAndMatch(targetId, userId, matchId);
        if (!deleted) {
            throw new StatTargetNotFoundException("Objetivo no encontrado.");
        }
    }
}
