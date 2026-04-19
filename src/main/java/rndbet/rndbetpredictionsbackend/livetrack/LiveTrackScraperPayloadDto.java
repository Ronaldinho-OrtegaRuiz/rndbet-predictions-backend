package rndbet.rndbetpredictionsbackend.livetrack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/** Cuerpo JSON que recibe el scraper (solo envío de ida). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LiveTrackScraperPayloadDto(
        @JsonProperty("fecha_referencia") String fechaReferencia,
        @JsonProperty("partidos") List<LiveTrackScraperPartidoDto> partidos) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LiveTrackScraperPartidoDto(
            @JsonProperty("match_id") int matchId,
            @JsonProperty("fecha") OffsetDateTime fecha,
            @JsonProperty("competicion") String competicion,
            @JsonProperty("equipo_local") String equipoLocal,
            @JsonProperty("equipo_visitante") String equipoVisitante,
            @JsonProperty("jornada") Integer jornada,
            @JsonProperty("fase") String fase,
            @JsonProperty("grupo") String grupo) {}
}
