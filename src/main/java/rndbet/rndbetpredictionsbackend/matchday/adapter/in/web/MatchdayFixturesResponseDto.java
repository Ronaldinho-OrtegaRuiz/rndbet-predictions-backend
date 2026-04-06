package rndbet.rndbetpredictionsbackend.matchday.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchdayFixturesResponseDto(
        @JsonProperty("jornada") Integer jornada,
        @JsonProperty("temporada_completada") Boolean temporadaCompletada,
        @JsonProperty("partidos") List<MatchdayFixtureLineDto> partidos
) {
}
