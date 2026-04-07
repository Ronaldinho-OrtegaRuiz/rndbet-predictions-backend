package rndbet.rndbetpredictionsbackend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class JwtAuthentication extends AbstractAuthenticationToken {

    private final long userId;
    private final String username;

    public JwtAuthentication(long userId, String username) {
        super(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.userId = userId;
        this.username = username;
        setAuthenticated(true);
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
