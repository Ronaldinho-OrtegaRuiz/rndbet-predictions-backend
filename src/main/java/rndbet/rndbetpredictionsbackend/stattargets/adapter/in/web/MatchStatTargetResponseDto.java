package rndbet.rndbetpredictionsbackend.stattargets.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import rndbet.rndbetpredictionsbackend.stattargets.domain.MatchStatTarget;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchStatTargetResponseDto(
        @JsonProperty("id") long id,
        @JsonProperty("estadistica") String estadistica,
        @JsonProperty("ambito") String ambito,
        @JsonProperty("umbral") int umbral,
        @JsonProperty("estado") String estado,
        @JsonProperty("creado_en") OffsetDateTime creadoEn,
        @JsonProperty("actualizado_en") OffsetDateTime actualizadoEn,
        @JsonProperty("cumplido_en") OffsetDateTime cumplidoEn,
        @JsonProperty("cumplido_minuto_partido") Integer cumplidoMinutoPartido,
        @JsonProperty("fallido_en") OffsetDateTime fallidoEn) {

    static MatchStatTargetResponseDto from(MatchStatTarget t) {
        return new MatchStatTargetResponseDto(
                t.id(),
                t.stat().apiValue(),
                t.scope().name(),
                t.threshold(),
                t.state().name(),
                t.createdAt(),
                t.updatedAt(),
                t.fulfilledAt(),
                t.fulfilledMatchMinute(),
                t.failedAt());
    }
}
