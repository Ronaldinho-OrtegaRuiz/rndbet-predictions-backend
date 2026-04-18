package rndbet.rndbetpredictionsbackend.stattargets.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateMatchStatTargetRequestDto(
        @JsonProperty("estadistica") String estadistica,
        @JsonProperty("ambito") String ambito,
        @JsonProperty("umbral") int umbral) {}
