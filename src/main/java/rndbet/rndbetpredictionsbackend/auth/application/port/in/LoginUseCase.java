package rndbet.rndbetpredictionsbackend.auth.application.port.in;

import rndbet.rndbetpredictionsbackend.auth.application.service.LoginResult;

public interface LoginUseCase {

    LoginResult login(String username, String rawPassword);
}
