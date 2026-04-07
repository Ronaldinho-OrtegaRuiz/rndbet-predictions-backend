package rndbet.rndbetpredictionsbackend.auth.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import rndbet.rndbetpredictionsbackend.auth.application.port.out.LoadUserCredentialsPort;
import rndbet.rndbetpredictionsbackend.auth.domain.UserCredentials;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserAuthPersistenceAdapter implements LoadUserCredentialsPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserCredentials> findByUsername(String username) {
        List<UserCredentials> rows = jdbcTemplate.query(
                """
                        SELECT id, username, password FROM users WHERE username = ?
                        """,
                (rs, rowNum) -> new UserCredentials(
                        rs.getLong("id"), rs.getString("username"), rs.getString("password")),
                username);
        return rows.stream().findFirst();
    }
}
