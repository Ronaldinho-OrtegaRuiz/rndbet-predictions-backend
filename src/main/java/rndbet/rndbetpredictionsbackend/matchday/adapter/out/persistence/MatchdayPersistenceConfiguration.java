package rndbet.rndbetpredictionsbackend.matchday.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.SeasonRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.TeamMatchStatsRepository;
import rndbet.rndbetpredictionsbackend.matchday.application.port.out.LoadMatchdayFixturesPort;

@Configuration
@Profile("!test")
public class MatchdayPersistenceConfiguration {

    @Bean
    LoadMatchdayFixturesPort loadMatchdayFixturesPort(
            SeasonRepository seasonRepository,
            MatchRepository matchRepository,
            TeamMatchStatsRepository teamMatchStatsRepository) {
        return new MatchdayJpaAdapter(seasonRepository, matchRepository, teamMatchStatsRepository);
    }
}
