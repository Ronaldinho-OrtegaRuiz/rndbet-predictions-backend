package rndbet.rndbetpredictionsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rndbet.rndbetpredictionsbackend.config.DotenvLoader;

@SpringBootApplication
public class RndbetPredictionsBackendApplication {

    public static void main(String[] args) {
        DotenvLoader.loadIntoSystemProperties();
        SpringApplication.run(RndbetPredictionsBackendApplication.class, args);
    }

}
