package rndbet.rndbetpredictionsbackend.auth.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rndbet.rndbetpredictionsbackend.auth.application.port.in.LoginUseCase;
import rndbet.rndbetpredictionsbackend.auth.application.service.LoginResult;
import rndbet.rndbetpredictionsbackend.security.JwtAuthentication;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto body) {
        LoginResult result = loginUseCase.login(body.username(), body.password());
        return LoginResponseDto.from(result);
    }

    @GetMapping("/me")
    public MeResponseDto me(Authentication authentication) {
        if (authentication instanceof JwtAuthentication jwt) {
            return new MeResponseDto(jwt.getUserId(), (String) jwt.getPrincipal());
        }
        throw new IllegalStateException("Autenticación JWT esperada");
    }
}
