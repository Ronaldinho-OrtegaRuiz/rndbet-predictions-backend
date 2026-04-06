package rndbet.rndbetpredictionsbackend.standings.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.standings.application.port.out.LoadStandingsDataPort;

@Configuration
@Profile("!test")
public class StandingsPersistenceConfiguration {

    @Bean
    LoadStandingsDataPort loadStandingsDataPort(JdbcTemplate jdbcTemplate) {
        return new StandingsPersistenceAdapter(jdbcTemplate);
    }
}
