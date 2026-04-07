package rndbet.rndbetpredictionsbackend.auth.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rndbet.rndbetpredictionsbackend.auth.application.exception.BadCredentialsException;

import java.net.URI;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthWebExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setTitle("No autorizado");
        pd.setType(URI.create("about:blank"));
        return pd;
    }
}
