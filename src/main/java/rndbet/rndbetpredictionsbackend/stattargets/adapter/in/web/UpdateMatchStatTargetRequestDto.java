package rndbet.rndbetpredictionsbackend.stattargets.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateMatchStatTargetRequestDto(@JsonProperty("umbral") int umbral) {}
