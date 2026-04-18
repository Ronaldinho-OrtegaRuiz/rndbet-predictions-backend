package rndbet.rndbetpredictionsbackend.stattargets.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.stattargets.domain.StatMetric;
import rndbet.rndbetpredictionsbackend.stattargets.domain.TargetScope;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meta")
public class StatTargetOptionsController {

    @GetMapping("/stat-target-options")
    public StatTargetOptionsResponseDto options() {
        List<String> stats =
                Arrays.stream(StatMetric.values()).map(StatMetric::apiValue).toList();
        List<String> scopes = Arrays.stream(TargetScope.values()).map(Enum::name).toList();
        return new StatTargetOptionsResponseDto(stats, scopes);
    }

    public record StatTargetOptionsResponseDto(
            @JsonProperty("estadisticas_permitidas") List<String> estadisticasPermitidas,
            @JsonProperty("ambitos") List<String> ambitos) {}
}
