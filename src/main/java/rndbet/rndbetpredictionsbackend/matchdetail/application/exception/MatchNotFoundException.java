package rndbet.rndbetpredictionsbackend.matchdetail.application.exception;

public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException(String message) {
        super(message);
    }
}
