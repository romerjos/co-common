package pe.gob.hospitalcayetano.cocommon.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exception404 extends RuntimeException {
    private String codigoError;

    public Exception404() {
        super();
    }

    public Exception404(String mensaje) {
        super(mensaje);
    }

    public Exception404(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }
}
