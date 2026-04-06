package rndbet.rndbetpredictionsbackend.matchday.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.time.Instant;

public record MatchdayFixtureLineDto(
        @JsonProperty("id") int id,
        @JsonProperty("fecha") Instant fecha,
        @JsonProperty("estado") String estado,
        @JsonProperty("equipo_local") String equipoLocal,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("logo_url_local")
        String logoUrlLocal,
        @JsonProperty("goles_local") Integer golesLocal,
        @JsonProperty("equipo_visitante") String equipoVisitante,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("logo_url_visitante")
        String logoUrlVisitante,
        @JsonProperty("goles_visitante") Integer golesVisitante,
        @JsonProperty("tarjetas_rojas_local") int tarjetasRojasLocal,
        @JsonProperty("tarjetas_rojas_visitante") int tarjetasRojasVisitante
) {
    static MatchdayFixtureLineDto from(MatchdayFixture f) {
        return new MatchdayFixtureLineDto(
                f.matchId(),
                f.date(),
                f.status(),
                f.homeTeamName(),
                f.homeTeamLogoUrl(),
                f.homeScore(),
                f.awayTeamName(),
                f.awayTeamLogoUrl(),
                f.awayScore(),
                f.homeRedCards(),
                f.awayRedCards());
    }
}
