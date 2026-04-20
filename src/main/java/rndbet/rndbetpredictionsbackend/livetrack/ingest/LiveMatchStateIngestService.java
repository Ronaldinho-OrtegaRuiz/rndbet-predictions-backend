package rndbet.rndbetpredictionsbackend.livetrack.ingest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEventEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchEventRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.TeamMatchStatsRepository;
import rndbet.rndbetpredictionsbackend.livetrack.ingest.LiveMatchStateIngestDtos.LiveMatchEventPayload;
import rndbet.rndbetpredictionsbackend.livetrack.ingest.LiveMatchStateIngestDtos.LiveMatchStateIngestRequest;
import rndbet.rndbetpredictionsbackend.livetrack.ingest.LiveMatchStateIngestDtos.LiveMatchStateIngestResponse;
import rndbet.rndbetpredictionsbackend.livetrack.ingest.LiveMatchStateIngestDtos.LiveTeamMatchStatsPayload;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class LiveMatchStateIngestService {

    private static final JsonMapper JSON = JsonMapper.shared();

    private final MatchRepository matchRepository;
    private final TeamMatchStatsRepository teamMatchStatsRepository;
    private final MatchEventRepository matchEventRepository;

    @Transactional
    public LiveMatchStateIngestResponse ingest(LiveMatchStateIngestRequest req) {
        MatchEntity match = matchRepository
                .findByIdWithTeams(req.matchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "match not found"));

        int homeId = match.getHomeTeam().getId();
        int awayId = match.getAwayTeam().getId();

        if (StringUtils.hasText(req.status())) {
            match.setStatus(req.status().trim().toUpperCase(Locale.ROOT));
        }
        if (req.homeScore() != null) {
            match.setHomeScore(req.homeScore());
        }
        if (req.awayScore() != null) {
            match.setAwayScore(req.awayScore());
        }
        if (req.currentMinute() != null) {
            match.setCurrentMinute(req.currentMinute());
        }
        if (req.addedTime() != null) {
            match.setAddedTime(req.addedTime());
        }
        match.setLastUpdated(OffsetDateTime.now());

        List<LiveTeamMatchStatsPayload> teamStats = req.teamStats();
        if (teamStats != null) {
            for (LiveTeamMatchStatsPayload row : teamStats) {
                if (row.teamId() != homeId && row.teamId() != awayId) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "team_stats.team_id must be home or away of this match");
                }
                if (row.home() && row.teamId() != homeId) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "team_stats.is_home does not match team_id");
                }
                if (!row.home() && row.teamId() != awayId) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "team_stats.is_home does not match team_id");
                }
                TeamMatchStatsEntity entity = teamMatchStatsRepository
                        .findByMatchIdAndTeamId(match.getId(), row.teamId())
                        .orElseGet(() -> {
                            TeamMatchStatsEntity e = new TeamMatchStatsEntity();
                            e.setMatchId(match.getId());
                            e.setTeamId(row.teamId());
                            return e;
                        });
                entity.setIsHome(row.home());
                if (row.goals() != null) {
                    entity.setGoals(row.goals());
                }
                if (row.possession() != null) {
                    entity.setPossession(row.possession());
                }
                if (row.shots() != null) {
                    entity.setShots(row.shots());
                }
                if (row.shotsOnTarget() != null) {
                    entity.setShotsOnTarget(row.shotsOnTarget());
                }
                if (row.saves() != null) {
                    entity.setSaves(row.saves());
                }
                if (row.yellowCards() != null) {
                    entity.setYellowCards(row.yellowCards());
                }
                if (row.redCards() != null) {
                    entity.setRedCards(row.redCards());
                }
                if (row.corners() != null) {
                    entity.setCorners(row.corners());
                }
                if (row.fouls() != null) {
                    entity.setFouls(row.fouls());
                }
                if (row.offsides() != null) {
                    entity.setOffsides(row.offsides());
                }
                teamMatchStatsRepository.save(entity);
            }
        }

        int inserted = 0;
        List<LiveMatchEventPayload> events = req.events();
        if (events != null) {
            for (LiveMatchEventPayload ev : events) {
                if (!StringUtils.hasText(ev.eventType())) {
                    continue;
                }
                Integer tid = ev.teamId();
                if (tid != null && tid != homeId && tid != awayId) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "events.team_id must be null or home or away of this match");
                }
                String type = ev.eventType().trim().toUpperCase(Locale.ROOT);
                String playerNameParam = StringUtils.hasText(ev.playerName()) ? ev.playerName().trim() : null;
                if (matchEventRepository.existsDuplicateLiveEvent(
                        match.getId(), ev.minute(), type, tid, playerNameParam)) {
                    continue;
                }
                MatchEventEntity row = new MatchEventEntity();
                row.setMatchId(match.getId());
                row.setMinute(ev.minute());
                row.setEventType(type);
                row.setTeamId(tid);
                row.setPlayer(null);
                row.setExtraDataJson(playerNameToExtraJson(playerNameParam));
                row.setCreatedAt(OffsetDateTime.now());
                matchEventRepository.save(row);
                inserted++;
            }
        }

        matchRepository.save(match);
        return new LiveMatchStateIngestResponse(true, match.getId(), inserted);
    }

    private static String playerNameToExtraJson(String trimmedName) {
        if (!StringUtils.hasText(trimmedName)) {
            return null;
        }
        try {
            return JSON.writeValueAsString(Map.of("player_name", trimmedName.trim()));
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "events.player_name invalid");
        }
    }
}
