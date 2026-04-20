package rndbet.rndbetpredictionsbackend.livetrack;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Autenticación del POST de estado en vivo que envía el scraper a este backend.
 */
@Data
@Component
@Profile("!test")
@ConfigurationProperties(prefix = "scraper.live-ingest")
public class LiveIngestProperties {

    /** Secreto compartido; debe coincidir con la cabecera {@code X-Live-Ingest-Key}. */
    private String apiKey = "";
}
