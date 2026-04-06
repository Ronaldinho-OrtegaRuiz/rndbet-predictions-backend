package rndbet.rndbetpredictionsbackend.web;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;

@RestControllerAdvice
public class JdbcConnectivityExceptionHandler {

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    ResponseEntity<ProblemDetail> handleDbUnreachable(CannotGetJdbcConnectionException ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String rootMsg = describeRootCause(root);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "PostgreSQL no disponible. " + rootMsg);
        pd.setTitle("Base de datos no disponible");
        pd.setType(URI.create("about:blank"));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(pd);
    }

    private static String describeRootCause(Throwable root) {
        String cls = root.getClass().getSimpleName();
        String msg = root.getMessage();
        if (root instanceof UnknownHostException) {
            return "No se resuelve el nombre del servidor (DNS). Host: "
                    + msg
                    + ". No es un tema de contraseña: tu PC no llega a resolver o contactar ese host (Internet, VPN, DNS, firewall, antivirus).";
        }
        if (root instanceof ConnectException) {
            return cls + ": " + (msg != null ? msg : "conexión rechazada");
        }
        return "Detalle: " + cls + (msg != null && !msg.isBlank() ? ": " + msg : "");
    }
}
