package rndbet.rndbetpredictionsbackend.matchday.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.matchday.application.port.out.LoadMatchdayFixturesPort;

@Configuration
@Profile("!test")
public class MatchdayPersistenceConfiguration {

    @Bean
    LoadMatchdayFixturesPort loadMatchdayFixturesPort(JdbcTemplate jdbcTemplate) {
        return new MatchdayPersistenceAdapter(jdbcTemplate);
    }
}
