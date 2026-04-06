package rndbet.rndbetpredictionsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import rndbet.rndbetpredictionsbackend.config.DotenvLoader;

@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class,
        DataJpaRepositoriesAutoConfiguration.class
})
public class RndbetPredictionsBackendApplication {

    public static void main(String[] args) {
        DotenvLoader.loadIntoSystemProperties();
        SpringApplication.run(RndbetPredictionsBackendApplication.class, args);
    }

}
