package rndbet.rndbetpredictionsbackend.matchday.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import rndbet.rndbetpredictionsbackend.db.MatchRoundStatusRow;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;
import rndbet.rndbetpredictionsbackend.jpa.entity.TeamMatchStatsEntity;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.SeasonRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.TeamMatchStatsRepository;
import rndbet.rndbetpredictionsbackend.jpa.support.EntityToRowMappers;
import rndbet.rndbetpredictionsbackend.matchday.application.port.out.LoadMatchdayFixturesPort;
import rndbet.rndbetpredictionsbackend.matchday.domain.MatchdayFixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class MatchdayJpaAdapter implements LoadMatchdayFixturesPort {

    private final SeasonRepository seasonRepository;
    private final MatchRepository matchRepository;
    private final TeamMatchStatsRepository teamMatchStatsRepository;

    @Override
    public boolean seasonBelongsToCompetition(int competitionId, int seasonId) {
        return seasonRepository.existsByIdAndCompetitionId(seasonId, competitionId);
    }

    @Override
    public Map<Integer, List<String>> loadMatchStatusesByRound(int seasonId) {
        Map<Integer, List<String>> map = new HashMap<>();
        for (MatchEntity me : matchRepository.findBySeason_Id(seasonId)) {
            if (me.getRound() == null) {
                continue;
            }
            map.computeIfAbsent(me.getRound(), k -> new ArrayList<>()).add(me.getStatus());
        }
        return map;
    }

    @Override
    public List<MatchdayFixture> loadMatchesForRound(int seasonId, int round) {
        List<MatchEntity> matches = matchRepository.findBySeasonAndRoundWithTeams(seasonId, round);
        List<Integer> ids = matches.stream().map(MatchEntity::getId).toList();
        List<TeamMatchStatsEntity> allStats = ids.isEmpty() ? List.of() : teamMatchStatsRepository.findByMatchIdIn(ids);
        return matches.stream()
                .map(m -> {
                    int homeId = m.getHomeTeam().getId();
                    int awayId = m.getAwayTeam().getId();
                    int mid = Objects.requireNonNull(m.getId());
                    int redHome = sumRedCardsForTeam(mid, homeId, allStats);
                    int redAway = sumRedCardsForTeam(mid, awayId, allStats);
                    return MatchdayJdbcMapper.toFixture(
                            EntityToRowMappers.toMatchRow(m),
                            m.getHomeTeam().getName(),
                            m.getHomeTeam().getLogoUrl(),
                            m.getAwayTeam().getName(),
                            m.getAwayTeam().getLogoUrl(),
                            redHome,
                            redAway);
                })
                .toList();
    }

    private static int sumRedCardsForTeam(int matchId, int teamId, List<TeamMatchStatsEntity> allStats) {
        return allStats.stream()
                .filter(s -> matchId == s.getMatchId() && teamId == s.getTeamId())
                .mapToInt(s -> s.getRedCards() == null ? 0 : s.getRedCards())
                .sum();
    }
}
