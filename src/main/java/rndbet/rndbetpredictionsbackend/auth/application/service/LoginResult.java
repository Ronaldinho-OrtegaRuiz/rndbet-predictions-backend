package rndbet.rndbetpredictionsbackend.auth.application.service;

public record LoginResult(String accessToken, String tokenType, long expiresInSeconds) {
}
