package rndbet.rndbetpredictionsbackend.auth.application.port.out;

import rndbet.rndbetpredictionsbackend.auth.domain.UserCredentials;

import java.util.Optional;

public interface LoadUserCredentialsPort {

    Optional<UserCredentials> findByUsername(String username);
}
