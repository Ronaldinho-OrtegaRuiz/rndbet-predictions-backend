package rndbet.rndbetpredictionsbackend.matchdetail.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;

@Configuration
@Profile("!test")
public class MatchDetailPersistenceConfiguration {

    @Bean
    LoadMatchDetailPort loadMatchDetailPort(JdbcTemplate jdbcTemplate) {
        return new MatchDetailPersistenceAdapter(jdbcTemplate);
    }
}
