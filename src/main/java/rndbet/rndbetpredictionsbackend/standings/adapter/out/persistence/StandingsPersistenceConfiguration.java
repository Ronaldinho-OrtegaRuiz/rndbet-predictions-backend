package rndbet.rndbetpredictionsbackend.standings.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rndbet.rndbetpredictionsbackend.jpa.repository.MatchRepository;
import rndbet.rndbetpredictionsbackend.jpa.repository.SeasonRepository;
import rndbet.rndbetpredictionsbackend.standings.application.port.out.LoadStandingsDataPort;

@Configuration
@Profile("!test")
public class StandingsPersistenceConfiguration {

    @Bean
    LoadStandingsDataPort loadStandingsDataPort(
            SeasonRepository seasonRepository, MatchRepository matchRepository) {
        return new StandingsJpaAdapter(seasonRepository, matchRepository);
    }
}
