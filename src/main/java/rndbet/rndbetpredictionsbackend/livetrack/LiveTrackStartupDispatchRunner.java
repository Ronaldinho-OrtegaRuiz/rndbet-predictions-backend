package rndbet.rndbetpredictionsbackend.livetrack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Al arrancar, ejecuta una vez el mismo envío que el job diario: solo incluye partidos del día en la zona
 * configurada con {@code live_track_enqueued = false}; si el cron ya los marcó, la consulta queda vacía y no
 * se llama al scraper.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class LiveTrackStartupDispatchRunner implements ApplicationRunner {

    private final LiveTrackProperties properties;
    private final LiveTrackDispatchService dispatchService;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isRunOnStartup()) {
            return;
        }
        if (!properties.isEnabled()) {
            return;
        }
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            return;
        }
        try {
            log.info("Live-track: comprobación al arranque (mismo envío que el cron si quedan partidos pendientes).");
            dispatchService.dispatchTodayForScraper();
        } catch (Exception ex) {
            log.error("Live-track: falló el envío al arranque.", ex);
        }
    }
}
