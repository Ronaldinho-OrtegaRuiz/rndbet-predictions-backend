package rndbet.rndbetpredictionsbackend.auth.adapter.in.web;

import rndbet.rndbetpredictionsbackend.auth.application.service.LoginResult;

public record LoginResponseDto(String accessToken, String tokenType, long expiresIn) {

    static LoginResponseDto from(LoginResult r) {
        return new LoginResponseDto(r.accessToken(), r.tokenType(), r.expiresInSeconds());
    }
}
