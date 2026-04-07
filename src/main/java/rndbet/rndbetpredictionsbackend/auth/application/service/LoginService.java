package rndbet.rndbetpredictionsbackend.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rndbet.rndbetpredictionsbackend.auth.application.exception.BadCredentialsException;
import rndbet.rndbetpredictionsbackend.auth.application.port.in.LoginUseCase;
import rndbet.rndbetpredictionsbackend.auth.application.port.out.LoadUserCredentialsPort;
import rndbet.rndbetpredictionsbackend.auth.domain.UserCredentials;
import rndbet.rndbetpredictionsbackend.security.JwtTokenService;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final LoadUserCredentialsPort loadUserCredentialsPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginResult login(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isEmpty()) {
            throw new BadCredentialsException();
        }
        UserCredentials user = loadUserCredentialsPort
                .findByUsername(username.trim())
                .orElseThrow(BadCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new BadCredentialsException();
        }
        String token = jwtTokenService.createToken(user.id(), user.username());
        return new LoginResult(token, "Bearer", jwtTokenService.getExpiresInSeconds());
    }
}
