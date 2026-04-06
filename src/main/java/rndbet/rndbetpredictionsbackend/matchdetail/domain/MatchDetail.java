package rndbet.rndbetpredictionsbackend.matchdetail.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record MatchDetail(
        int matchId,
        Instant fecha,
        String estado,
        String equipoLocal,
        String equipoVisitante,
        Integer golesLocal,
        Integer golesVisitante,
        Map<String, Object> estadisticasLocal,
        Map<String, Object> estadisticasVisitante,
        List<MatchEventLine> eventos
) {}
