package rndbet.rndbetpredictionsbackend.standings.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import rndbet.rndbetpredictionsbackend.jpa.entity.MatchEntity;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.SeasonRepository;
import rndbet.rndbetpredictionsbackend.jpa.support.EntityToRowMappers;
import rndbet.rndbetpredictionsbackend.standings.application.port.out.LoadStandingsDataPort;
import rndbet.rndbetpredictionsbackend.standings.domain.FinishedMatchForStandings;

import java.util.List;

@RequiredArgsConstructor
public class StandingsJpaAdapter implements LoadStandingsDataPort {

    private final SeasonRepository seasonRepository;
    private final MatchRepository matchRepository;

    @Override
    public boolean seasonBelongsToCompetition(int competitionId, int seasonId) {
        return seasonRepository.existsByIdAndCompetitionId(seasonId, competitionId);
    }

    @Override
    public List<FinishedMatchForStandings> loadFinishedMatchesBySeason(int seasonId) {
        return matchRepository.findFinishedBySeasonWithTeams(seasonId).stream()
                .map(StandingsJpaAdapter::toFinished)
                .toList();
    }

    private static FinishedMatchForStandings toFinished(MatchEntity m) {
        return StandingsJdbcMapper.toFinishedMatch(
                EntityToRowMappers.toMatchRow(m),
                m.getHomeTeam().getName(),
                m.getHomeTeam().getLogoUrl(),
                m.getAwayTeam().getName(),
                m.getAwayTeam().getLogoUrl());
    }
}
