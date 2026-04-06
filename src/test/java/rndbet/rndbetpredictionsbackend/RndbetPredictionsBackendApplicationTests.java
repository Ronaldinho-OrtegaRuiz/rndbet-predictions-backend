package rndbet.rndbetpredictionsbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import rndbet.rndbetpredictionsbackend.matchday.application.port.out.LoadMatchdayFixturesPort;
import rndbet.rndbetpredictionsbackend.matchdetail.application.port.out.LoadMatchDetailPort;
import rndbet.rndbetpredictionsbackend.standings.application.port.out.LoadStandingsDataPort;

@SpringBootTest(
        properties = {
            "spring.autoconfigure.exclude="
                    + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                    + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                    + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                    + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                    + "org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration,"
                    + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
        })
@ActiveProfiles("test")
class RndbetPredictionsBackendApplicationTests {

    @MockitoBean
    LoadStandingsDataPort loadStandingsDataPort;

    @MockitoBean
    LoadMatchdayFixturesPort loadMatchdayFixturesPort;

    @MockitoBean
    LoadMatchDetailPort loadMatchDetailPort;

    @Test
    void contextLoads() {
    }

}
