package rndbet.rndbetpredictionsbackend.matchdetail.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchEventRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.SeasonRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.TeamMatchStatsRepository;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;

@Configuration
@Profile("!test")
public class MatchDetailPersistenceConfiguration {

    @Bean
    LoadMatchDetailPort loadMatchDetailPort(
            MatchRepository matchRepository,
            SeasonRepository seasonRepository,
            TeamMatchStatsRepository teamMatchStatsRepository,
            MatchEventRepository matchEventRepository) {
        return new MatchDetailJpaAdapter(matchRepository, seasonRepository, teamMatchStatsRepository, matchEventRepository);
    }
}
