package rndbet.rndbetpredictionsbackend.matchdetail.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import rndbet.rndbetpredictionsbackend.db.TeamMatchStatsRow;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEventEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchEventRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.SeasonRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.TeamMatchStatsRepository;
import rndbet.rndbetpredictionsbackend.jpa.support.EntityToRowMappers;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchDetail;
import rndbet.rndbetpredictionsbackend.matchdetail.domain.MatchEventLine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class MatchDetailJpaAdapter implements LoadMatchDetailPort {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final TeamMatchStatsRepository teamMatchStatsRepository;
    private final MatchEventRepository matchEventRepository;

    @Override
    public boolean seasonBelongsToCompetition(int competitionId, int seasonId) {
        return seasonRepository.existsByIdAndCompetitionId(seasonId, competitionId);
    }

    @Override
    public boolean matchExistsInSeasonRound(int competitionId, int seasonId, int round, int matchId) {
        return matchRepository.countInSeasonRoundContext(matchId, seasonId, round, competitionId) > 0;
    }

    @Override
    public Optional<MatchDetail> loadMatchDetail(int competitionId, int seasonId, int round, int matchId) {
        Optional<MatchEntity> header =
                matchRepository.findForDetailHeader(matchId, seasonId, round, competitionId);
        if (header.isEmpty()) {
            return Optional.empty();
        }
        MatchEntity m = header.get();
        int homeTeamId = m.getHomeTeam().getId();
        int awayTeamId = m.getAwayTeam().getId();
        Map<String, Object> homeStats = new LinkedHashMap<>();
        Map<String, Object> awayStats = new LinkedHashMap<>();
        for (TeamMatchStatsEntity row : teamMatchStatsRepository.findByMatchId(matchId)) {
            TeamMatchStatsRow tr = EntityToRowMappers.toTeamMatchStatsRow(row);
            MatchDetailJdbcMapper.applyTeamStatRow(tr, homeTeamId, awayTeamId, homeStats, awayStats);
        }
        List<MatchEventLine> eventos =
                matchEventRepository.findAllForMatchWithPlayer(matchId).stream()
                        .map(me -> toEventLine(me, homeTeamId, awayTeamId))
                        .toList();
        MatchDetailHeaderBundle h = new MatchDetailHeaderBundle(
                EntityToRowMappers.toMatchRow(m),
                m.getHomeTeam().getName(),
                m.getAwayTeam().getName(),
                m.getHomeTeam().getLogoUrl(),
                m.getAwayTeam().getLogoUrl());
        return Optional.of(MatchDetailJdbcMapper.toDetail(h, homeStats, awayStats, eventos));
    }

    private static MatchEventLine toEventLine(MatchEventEntity me, int homeTeamId, int awayTeamId) {
        String jugador = me.getPlayer() != null ? me.getPlayer().getName() : null;
        Integer tid = me.getTeamId();
        String lado = null;
        if (tid != null) {
            if (tid == homeTeamId) {
                lado = "local";
            } else if (tid == awayTeamId) {
                lado = "visitante";
            }
        }
        return new MatchEventLine(me.getMinute(), me.getEventType(), jugador, lado);
    }
}
