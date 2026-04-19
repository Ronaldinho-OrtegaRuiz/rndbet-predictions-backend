package rndbet.rndbetpredictionsbackend.livetrack;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Configuración del envío diario de partidos al scraper. La ruta HTTP completa va en {@code url}
 * (ej. {@code https://scraper/ejemplo/ruta-pendiente} cuando la definas).
 */
@Data
@Component
@Profile("!test")
@ConfigurationProperties(prefix = "scraper.live-track")
public class LiveTrackProperties {

    /** Si es false, no se ejecuta el job ni se llama al scraper. */
    private boolean enabled = false;

    /** URL absoluta del endpoint del scraper (POST JSON). Vacío = no enviar. */
    private String url = "";

    /** Expresión cron (Spring 6 campos: segundo minuto hora día mes día-semana). */
    private String dispatchCron = "0 0 5 * * ?";

    /** Zona horaria del calendario del “día” y del disparador del cron. */
    private String timeZone = "America/Bogota";

    /**
     * Si es true, al levantar el backend se intenta una vez el mismo POST del día (solo partidos aún no
     * {@code live_track_enqueued}).
     */
    private boolean runOnStartup = true;
}
