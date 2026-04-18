package rndbet.rndbetpredictionsbackend.auth.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import rndbet.rndbetpredictionsbackend.auth.application.port.out.LoadUserCredentialsPort;
import rndbet.rndbetpredictionsbackend.auth.domain.UserCredentials;
import rndbet.rndbetpredictionsbackend.jpa.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
public class UserAuthJpaAdapter implements LoadUserCredentialsPort {

    private final UserRepository userRepository;

    @Override
    public Optional<UserCredentials> findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .map(u -> new UserCredentials(u.getId(), u.getUsername(), u.getPassword()));
    }
}
