package rndbet.rndbetpredictionsbackend.stattargets.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.out.UserMatchStatTargetsPort;

@Configuration
@Profile("!test")
public class MatchStatTargetsPersistenceConfiguration {

    @Bean
    UserMatchStatTargetsPort userMatchStatTargetsPort(JdbcTemplate jdbcTemplate) {
        return new MatchStatTargetsPersistenceAdapter(jdbcTemplate);
    }
}
