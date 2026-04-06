package rndbet.rndbetpredictionsbackend.matchdetail.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchEventLine;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchEventDto(
        @JsonProperty("tipo") String tipo,
        @JsonProperty("minuto") Integer minuto,
        @JsonProperty("jugador") String jugador,
        @JsonProperty("lado") String lado
) {
    static MatchEventDto from(MatchEventLine e) {
        return new MatchEventDto(e.tipo(), e.minuto(), e.jugador(), e.lado());
    }
}
