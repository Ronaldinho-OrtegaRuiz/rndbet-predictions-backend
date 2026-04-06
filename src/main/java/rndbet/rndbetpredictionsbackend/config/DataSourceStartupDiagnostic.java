package rndbet.rndbetpredictionsbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DataSourceStartupDiagnostic {

    @Bean
    @ConditionalOnProperty(name = "rndbet.log-effective-jdbc-url", havingValue = "true")
    ApplicationRunner logEffectiveJdbcUrl(
            @Value("${spring.datasource.url}") String jdbcUrl,
            @Value("${spring.datasource.username}") String username) {
        return args -> log.warn(
                "rndbet diagnostics — spring.datasource.url={} username={} (comprueba que no sea localhost si usás Supabase)",
                jdbcUrl,
                username);
    }
}
