package rndbet.rndbetpredictionsbackend.standings.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import rndbet.rndbetpredictionsbackend.standings.domain.FormLetter;
import rndbet.rndbetpredictionsbackend.standings.domain.StandingRow;

import java.util.List;

public record StandingLineDto(
        @JsonProperty("posicion") int posicion,
        @JsonProperty("equipo_id") int equipoId,
        @JsonProperty("equipo") String equipo,
        @JsonProperty("partidos_jugados") int partidosJugados,
        @JsonProperty("partidos_ganados") int partidosGanados,
        @JsonProperty("partidos_empatados") int partidosEmpatados,
        @JsonProperty("partidos_perdidos") int partidosPerdidos,
        @JsonProperty("goles_a_favor") int golesAFavor,
        @JsonProperty("goles_en_contra") int golesEnContra,
        @JsonProperty("diferencia_goles") int diferenciaGoles,
        @JsonProperty("puntos") int puntos,
        @JsonProperty("forma") List<FormLetter> forma
) {
    static StandingLineDto from(StandingRow row) {
        return new StandingLineDto(
                row.position(),
                row.teamId(),
                row.teamName(),
                row.played(),
                row.won(),
                row.drawn(),
                row.lost(),
                row.goalsFor(),
                row.goalsAgainst(),
                row.goalDifference(),
                row.points(),
                row.form());
    }
}
