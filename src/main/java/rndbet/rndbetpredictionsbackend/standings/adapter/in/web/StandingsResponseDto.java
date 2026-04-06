package rndbet.rndbetpredictionsbackend.standings.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StandingsResponseDto(@JsonProperty("tabla") List<StandingLineDto> tabla) {
}
