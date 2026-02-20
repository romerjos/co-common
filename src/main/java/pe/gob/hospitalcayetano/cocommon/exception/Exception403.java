package pe.gob.hospitalcayetano.cocommon.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exception403 extends RuntimeException {
    private String codigoError;

    public Exception403() {
        super();
    }

    public Exception403(String mensaje) {
        super(mensaje);
    }

    public Exception403(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }
}
