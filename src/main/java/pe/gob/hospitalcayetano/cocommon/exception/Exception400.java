package pe.gob.hospitalcayetano.cocommon.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exception400 extends RuntimeException {
    private String codigoError;

    public Exception400() {
        super();
    }

    public Exception400(String mensaje) {
        super(mensaje);
    }

    public Exception400(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }
}
