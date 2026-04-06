package rndbet.rndbetpredictionsbackend.matchdetail.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchDetailResponseDto(
        @JsonProperty("id") int id,
        @JsonProperty("fecha") Instant fecha,
        @JsonProperty("estado") String estado,
        @JsonProperty("equipo_local") String equipoLocal,
        @JsonProperty("logo_url_local") String logoUrlLocal,
        @JsonProperty("equipo_visitante") String equipoVisitante,
        @JsonProperty("logo_url_visitante") String logoUrlVisitante,
        @JsonProperty("goles_local") Integer golesLocal,
        @JsonProperty("goles_visitante") Integer golesVisitante,
        @JsonProperty("estadisticas_local") Map<String, Object> estadisticasLocal,
        @JsonProperty("estadisticas_visitante") Map<String, Object> estadisticasVisitante,
        @JsonProperty("eventos") List<MatchEventDto> eventos
) {
    static MatchDetailResponseDto from(MatchDetail d) {
        return new MatchDetailResponseDto(
                d.matchId(),
                d.fecha(),
                d.estado(),
                d.equipoLocal(),
                d.logoUrlLocal(),
                d.equipoVisitante(),
                d.logoUrlVisitante(),
                d.golesLocal(),
                d.golesVisitante(),
                d.estadisticasLocal(),
                d.estadisticasVisitante(),
                d.eventos().stream().map(MatchEventDto::from).toList());
    }
}
