package rndbet.rndbetpredictionsbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenService {

    private static final String CLAIM_USER_ID = "uid";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:259200000}")
    private long expirationMs;

    private SecretKey signingKey;
    private long expiresInSeconds;

    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "jwt.secret está vacío: define SECRET_TOKEN_JWT en .env o jwt.secret en configuración");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret debe tener al menos 32 bytes (256 bits) para HS256; alarga SECRET_TOKEN_JWT");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiresInSeconds = Math.max(1L, expirationMs / 1000L);
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String createToken(long userId, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_USER_ID, userId)
                .issuedAt(now)
                .expiration(exp)
                .signWith(signingKey)
                .compact();
    }

    public ParsedJwt parseValid(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String subject = claims.getSubject();
        Object uid = claims.get(CLAIM_USER_ID);
        if (subject == null || subject.isBlank() || uid == null) {
            throw new JwtException("Token sin subject o uid");
        }
        long userId;
        if (uid instanceof Number n) {
            userId = n.longValue();
        } else {
            userId = Long.parseLong(uid.toString());
        }
        return new ParsedJwt(userId, subject);
    }

    public record ParsedJwt(long userId, String username) {
    }
}
