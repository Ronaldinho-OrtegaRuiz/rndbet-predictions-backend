package rndbet.rndbetpredictionsbackend.stattargets.application.exception;

/** Objetivo ya cumplido o fallido: no se permite cambiar el umbral. */
public final class StatTargetNotEditableException extends RuntimeException {

    public StatTargetNotEditableException(String message) {
        super(message);
    }
}
