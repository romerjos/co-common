package pe.gob.hospitalcayetano.cocommon.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exception409 extends RuntimeException {
    private String codigoError;

    public Exception409() {
        super();
    }

    public Exception409(String mensaje) {
        super(mensaje);
    }

    public Exception409(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }
}
