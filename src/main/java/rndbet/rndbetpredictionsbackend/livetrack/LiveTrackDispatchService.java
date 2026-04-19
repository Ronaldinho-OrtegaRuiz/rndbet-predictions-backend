package rndbet.rndbetpredictionsbackend.livetrack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class LiveTrackDispatchService {

    private final LiveTrackProperties properties;
    private final LiveTrackDayMatchSelection dayMatchSelection;
    private final MatchRepository matchRepository;

    /**
     * Partidos del día calendario (zona configurada) aún no enqueued; POST JSON al scraper; marca envío OK.
     */
    @Transactional
    public void dispatchTodayForScraper() {
        if (!properties.isEnabled()) {
            log.debug("Live-track: deshabilitado (scraper.live-track.enabled=false).");
            return;
        }
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            log.warn("Live-track: no se envía nada porque scraper.live-track.url está vacío.");
            return;
        }

        ZoneId zone = ZoneId.of(properties.getTimeZone());
        LocalDate today = LocalDate.now(zone);
        OffsetDateTime start = today.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime end = today.plusDays(1).atStartOfDay(zone).toOffsetDateTime();

        List<LiveTrackMatchOutbound> rows = dayMatchSelection.findPendingBetween(start, end);
        if (rows.isEmpty()) {
            log.info("Live-track: 0 partidos pendientes de envío para {} ({}).", today, zone);
            return;
        }

        List<LiveTrackScraperPayloadDto.LiveTrackScraperPartidoDto> partidos = rows.stream()
                .map(r -> new LiveTrackScraperPayloadDto.LiveTrackScraperPartidoDto(
                        r.matchId(),
                        r.fecha(),
                        r.competicion(),
                        r.equipoLocal(),
                        r.equipoVisitante(),
                        r.jornada(),
                        r.fase(),
                        r.grupo()))
                .toList();

        LiveTrackScraperPayloadDto body = new LiveTrackScraperPayloadDto(today.toString(), partidos);

        RestClient.create()
                .post()
                .uri(properties.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Integer> ids = rows.stream().map(LiveTrackMatchOutbound::matchId).toList();
        int updated = matchRepository.markLiveTrackEnqueued(ids, now);
        log.info(
                "Live-track: enviados {} partidos para {} ({}). Marcados enqueued: {}.",
                rows.size(),
                today,
                zone,
                updated);
    }
}
