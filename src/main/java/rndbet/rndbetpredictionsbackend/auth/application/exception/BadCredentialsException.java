package rndbet.rndbetpredictionsbackend.auth.application.exception;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {
        super("Credenciales inválidas");
    }
}
