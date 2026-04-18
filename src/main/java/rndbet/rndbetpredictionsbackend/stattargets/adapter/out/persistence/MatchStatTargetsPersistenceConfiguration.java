package rndbet.rndbetpredictionsbackend.stattargets.adapter.out.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import rndbet.rndbetpredictionsbackend.jpa.repository.UserMatchStatTargetRepository;
import rndbet.rndbetpredictionsbackend.stattargets.application.port.out.UserMatchStatTargetsPort;

@Configuration
@Profile("!test")
public class MatchStatTargetsPersistenceConfiguration {

    @Bean
    UserMatchStatTargetsPort userMatchStatTargetsPort(UserMatchStatTargetRepository repository) {
        return new MatchStatTargetsJpaAdapter(repository);
    }
}
