package rndbet.rndbetpredictionsbackend.auth.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.auth.application.port.out.LoadUserCredentialsPort;

@Configuration
@Profile("!test")
public class UserAuthPersistenceConfiguration {

    @Bean
    LoadUserCredentialsPort loadUserCredentialsPort(JdbcTemplate jdbcTemplate) {
        return new UserAuthPersistenceAdapter(jdbcTemplate);
    }
}
