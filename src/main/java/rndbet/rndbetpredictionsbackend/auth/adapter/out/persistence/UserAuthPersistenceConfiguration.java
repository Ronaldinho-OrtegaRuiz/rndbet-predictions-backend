package rndbet.rndbetpredictionsbackend.auth.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rndbet.rndbetpredictionsbackend.auth.application.port.out.LoadUserCredentialsPort;
import rndbet.rndbetpredictionsbackend.jpa.repository.UserRepository;

@Configuration
@Profile("!test")
public class UserAuthPersistenceConfiguration {

    @Bean
    LoadUserCredentialsPort loadUserCredentialsPort(UserRepository userRepository) {
        return new UserAuthJpaAdapter(userRepository);
    }
}
