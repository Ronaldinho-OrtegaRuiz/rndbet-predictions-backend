package rndbet.rndbetpredictionsbackend.livetrack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class LiveTrackDailyJob {

    private final LiveTrackProperties properties;
    private final LiveTrackDispatchService dispatchService;

    @Scheduled(
            cron = "${scraper.live-track.dispatch-cron:0 0 5 * * ?}",
            zone = "${scraper.live-track.time-zone:America/Bogota}")
    public void run() {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            dispatchService.dispatchTodayForScraper();
        } catch (Exception ex) {
            log.error("Live-track: falló el job diario de envío al scraper.", ex);
        }
    }
}
