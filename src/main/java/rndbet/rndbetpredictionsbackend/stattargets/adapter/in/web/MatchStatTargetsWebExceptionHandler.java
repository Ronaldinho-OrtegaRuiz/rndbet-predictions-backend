package rndbet.rndbetpredictionsbackend.stattargets.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rndbet.rndbetpredictionsbackend.matchdetail.application.exception.MatchNotFoundException;
import rndbet.rndbetpredictionsbackend.standings.application.exception.SeasonNotFoundException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.InvalidStatTargetException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.StatTargetDuplicateException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.StatTargetNotEditableException;
import rndbet.rndbetpredictionsbackend.stattargets.application.exception.StatTargetNotFoundException;

import java.net.URI;

@RestControllerAdvice(
        assignableTypes = {MatchStatTargetsController.class, StatTargetOptionsController.class})
public class MatchStatTargetsWebExceptionHandler {

    @ExceptionHandler(SeasonNotFoundException.class)
    ProblemDetail handleSeasonNotFound(SeasonNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Temporada no encontrada");
        pd.setType(URI.create("about:blank"));
        return pd;
    }

    @ExceptionHandler(MatchNotFoundException.class)
    ProblemDetail handleMatchNotFound(MatchNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Partido no encontrado");
        pd.setType(URI.create("about:blank"));
        return pd;
    }

    @ExceptionHandler(StatTargetNotFoundException.class)
    ProblemDetail handleTargetNotFound(StatTargetNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Objetivo no encontrado");
        pd.setType(URI.create("about:blank"));
        return pd;
    }

    @ExceptionHandler(InvalidStatTargetException.class)
    ProblemDetail handleInvalid(InvalidStatTargetException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Solicitud inválida");
        pd.setType(URI.create("about:blank"));
        return pd;
    }

    @ExceptionHandler(StatTargetDuplicateException.class)
    ProblemDetail handleDuplicate(StatTargetDuplicateException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Objetivo duplicado");
        pd.setType(URI.create("about:blank"));
        return pd;
    }

    @ExceptionHandler(StatTargetNotEditableException.class)
    ProblemDetail handleNotEditable(StatTargetNotEditableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Objetivo no editable");
        pd.setType(URI.create("about:blank"));
        return pd;
    }
}
