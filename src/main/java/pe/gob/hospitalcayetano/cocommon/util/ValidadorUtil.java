package pe.gob.hospitalcayetano.cocommon.util;

import org.springframework.beans.BeanUtils;
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
    }

    public static void existContent(Object object) {
        if (object == null) {
            throw new Exception404();
        }
    }

    public static void existContent(List list) {
        if (list == null || list.isEmpty()) {
            throw new Exception404();
        }
    }

    public static void existContent(long count) {
        if (count == 0) {
            throw new Exception404();
        }
    }

    public static void validarCodigoHttp(Integer statuCode) {
        switch (HttpStatus.valueOf(statuCode)) {
            case BAD_REQUEST:
                throw new Exception400();
            case NOT_FOUND:
                throw new Exception404();
            case CONFLICT:
                throw new Exception409();
            case INTERNAL_SERVER_ERROR:
                throw new RuntimeException();
        }
    }

    public static void validarCodigoHttp(Integer statuCode, String mensaje) {
        switch (HttpStatus.valueOf(statuCode)) {
            case BAD_REQUEST:
                throw new Exception400(mensaje);
            case NOT_FOUND:
                throw new Exception404(mensaje);
            case CONFLICT:
                throw new Exception409(mensaje);
            case INTERNAL_SERVER_ERROR:
                throw new RuntimeException(mensaje);
        }
    }

    public static void validarCodigoHttp(Object object) {
        Metadata metadata = new Metadata();
        BeanUtils.copyProperties(object, metadata);

        switch (HttpStatus.valueOf(metadata.getStatus())) {
            case BAD_REQUEST:
                throw new Exception400(metadata.getMessage());
            case NOT_FOUND:
                throw new Exception404(metadata.getMessage());
            case CONFLICT:
                throw new Exception409(metadata.getMessage());
            case INTERNAL_SERVER_ERROR:
                throw new RuntimeException(metadata.getMessage());
        }
    }

    public static void validarCodigoHttp(Object object, HttpHeaders headers) {
        Metadata metadata = new Metadata();
        BeanUtils.copyProperties(object, metadata);
        List<String> codeErrors = headers.get("Code-Error");
        String codeError = "";

        if (Objects.nonNull(codeErrors) && !codeErrors.isEmpty()) {
            codeError = codeErrors.get(0);
        }

        switch (HttpStatus.valueOf(metadata.getStatus())) {
            case BAD_REQUEST:
                throw new Exception400(metadata.getMessage(), codeError);
            case NOT_FOUND:
                throw new Exception404(metadata.getMessage(), codeError);
            case CONFLICT:
                throw new Exception409(metadata.getMessage(), codeError);
            case INTERNAL_SERVER_ERROR:
                throw new RuntimeException(metadata.getMessage());
        }
    }
}
