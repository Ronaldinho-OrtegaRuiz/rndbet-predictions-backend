package rndbet.rndbetpredictionsbackend.livetrack.ingest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public final class LiveMatchStateIngestDtos {

    private LiveMatchStateIngestDtos() {}

    public record LiveMatchStateIngestRequest(
            @JsonProperty("match_id") int matchId,
            @JsonProperty("status") String status,
            @JsonProperty("home_score") Integer homeScore,
            @JsonProperty("away_score") Integer awayScore,
            @JsonProperty("current_minute") Integer currentMinute,
            @JsonProperty("added_time") Integer addedTime,
            @JsonProperty("team_stats") List<LiveTeamMatchStatsPayload> teamStats,
            @JsonProperty("events") List<LiveMatchEventPayload> events) {}

    public record LiveTeamMatchStatsPayload(
            @JsonProperty("team_id") int teamId,
            @JsonProperty("is_home") boolean home,
            @JsonProperty("goals") Integer goals,
            @JsonProperty("possession") BigDecimal possession,
            @JsonProperty("shots") Integer shots,
            @JsonProperty("shots_on_target") Integer shotsOnTarget,
            @JsonProperty("saves") Integer saves,
            @JsonProperty("yellow_cards") Integer yellowCards,
            @JsonProperty("red_cards") Integer redCards,
            @JsonProperty("corners") Integer corners,
            @JsonProperty("fouls") Integer fouls,
            @JsonProperty("offsides") Integer offsides) {}

    public record LiveMatchEventPayload(
            @JsonProperty("minute") Integer minute,
            @JsonProperty("event_type") String eventType,
            @JsonProperty("team_id") Integer teamId,
            @JsonProperty("player_name") String playerName) {}

    public record LiveMatchStateIngestResponse(boolean ok, int matchId, int eventsInserted) {}
}
