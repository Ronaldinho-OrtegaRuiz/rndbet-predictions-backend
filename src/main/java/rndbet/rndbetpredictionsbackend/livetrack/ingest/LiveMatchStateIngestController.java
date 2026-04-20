package rndbet.rndbetpredictionsbackend.livetrack.ingest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.livetrack.LiveIngestProperties;
import rndbet.rndbetpredictionsbackend.livetrack.ingest.LiveMatchStateIngestDtos.LiveMatchStateIngestRequest;
import rndbet.rndbetpredictionsbackend.livetrack.ingest.LiveMatchStateIngestDtos.LiveMatchStateIngestResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/live-track")
@Profile("!test")
@RequiredArgsConstructor
public class LiveMatchStateIngestController {

    public static final String LIVE_INGEST_KEY_HEADER = "X-Live-Ingest-Key";

    private final LiveIngestProperties liveIngestProperties;
    private final LiveMatchStateIngestService liveMatchStateIngestService;

    @PostMapping("/match-state")
    public ResponseEntity<?> ingest(
            @RequestHeader(value = LIVE_INGEST_KEY_HEADER, required = false) String apiKey,
            @RequestBody LiveMatchStateIngestRequest body) {
        String expected = liveIngestProperties.getApiKey();
        if (!StringUtils.hasText(expected)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "live_ingest_not_configured"));
        }
        if (!constantTimeEquals(expected, apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        LiveMatchStateIngestResponse result = liveMatchStateIngestService.ingest(body);
        return ResponseEntity.ok(result);
    }

    private static boolean constantTimeEquals(String expected, String given) {
        if (given == null) {
            given = "";
        }
        byte[] a = expected.getBytes(StandardCharsets.UTF_8);
        byte[] b = given.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
