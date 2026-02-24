package pe.gob.hospitalcayetano.cocommon.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import pe.gob.hospitalcayetano.cocommon.exception.Exception400;
import pe.gob.hospitalcayetano.cocommon.exception.Exception404;
import pe.gob.hospitalcayetano.cocommon.exception.Exception409;
import pe.gob.hospitalcayetano.cocommon.model.Metadata;

import java.util.List;
import java.util.Objects;

public final class ValidadorUtil {

    private ValidadorUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void existContent(Object object) {
        if (object == null) {
            throw new Exception404();
        }
    }

    public static void existContent(List<?> list) {
        if (list == null || list.isEmpty()) {
            throw new Exception404();
        }
    }

    public static void existContent(long count) {
        if (count == 0) {
            throw new Exception404();
        }
    }

    public static void validarCodigoHttp(Integer statusCode) {
        validarCodigoHttp(statusCode, null);
    }

    public static void validarCodigoHttp(Integer statusCode, String mensaje) {
        if (statusCode == null) return;

        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) return;

        switch (status) {
            case BAD_REQUEST -> throw (mensaje == null ? new Exception400() : new Exception400(mensaje));
            case NOT_FOUND -> throw (mensaje == null ? new Exception404() : new Exception404(mensaje));
            case CONFLICT -> throw (mensaje == null ? new Exception409() : new Exception409(mensaje));
            case INTERNAL_SERVER_ERROR -> throw (mensaje == null ? new RuntimeException() : new RuntimeException(mensaje));
            default -> { /* Otros códigos no lanzan excepción */ }
        }
    }

    public static void validarCodigoHttp(Metadata metadata) {
        if (metadata == null) return;
        validarCodigoHttp(metadata.getStatus(), metadata.getMessage());
    }

    public static void validarCodigoHttp(Metadata metadata, HttpHeaders headers) {
        if (metadata == null) return;

        String codeError = "";
        if (headers != null) {
            List<String> codeErrors = headers.get("Code-Error");
            if (Objects.nonNull(codeErrors) && !codeErrors.isEmpty()) {
                codeError = codeErrors.get(0); // Primer código de error
            }
        }

        HttpStatus status = HttpStatus.resolve(metadata.getStatus());
        if (status == null) return;

        switch (status) {
            case BAD_REQUEST -> throw new Exception400(metadata.getMessage(), codeError);
            case NOT_FOUND -> throw new Exception404(metadata.getMessage(), codeError);
            case CONFLICT -> throw new Exception409(metadata.getMessage(), codeError);
            case INTERNAL_SERVER_ERROR -> throw new RuntimeException(metadata.getMessage());
            default -> { /* Otros códigos no lanzan excepción */ }
        }
    }
}