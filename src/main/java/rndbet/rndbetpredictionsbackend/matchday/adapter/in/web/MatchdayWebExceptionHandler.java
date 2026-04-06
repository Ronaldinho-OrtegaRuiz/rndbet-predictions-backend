package rndbet.rndbetpredictionsbackend.matchday.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rndbet.rndbetpredictionsbackend.standings.application.exception.SeasonNotFoundException;

import java.net.URI;

@RestControllerAdvice(assignableTypes = MatchdayController.class)
public class MatchdayWebExceptionHandler {

    @ExceptionHandler(SeasonNotFoundException.class)
    ProblemDetail handleSeasonNotFound(SeasonNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Temporada no encontrada");
        pd.setType(URI.create("about:blank"));
        return pd;
    }
}
