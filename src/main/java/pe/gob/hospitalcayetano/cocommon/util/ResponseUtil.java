package pe.gob.hospitalcayetano.cocommon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.gob.hospitalcayetano.cocommon.model.ApiDataResponse401;
import pe.gob.hospitalcayetano.cocommon.model.ApiDataResponse403;
import pe.gob.hospitalcayetano.cocommon.model.ApiResponse401;
import pe.gob.hospitalcayetano.cocommon.model.ApiResponse403;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class ResponseUtil {


    private ResponseUtil() {
    }

    public static <T> ResponseEntity<T> obtenerResultado(Object data, Class<T> classResponse) {
        return new ResponseEntity<>(
                obtenerContenido(data, classResponse),
                HttpStatus.OK
        );
    }

    public static <T> ResponseEntity<T> obtenerResultado(Class<T> classResponse) {
        return new ResponseEntity<>(
                obtenerContenido(classResponse),
                HttpStatus.OK
        );
    }

    @SneakyThrows
    private static <T> T obtenerContenido(Object data, Class<T> classResponse) {
        try {
            T classDestinoNuevo = classResponse.newInstance();

            Field fieldMetadata = classDestinoNuevo.getClass().getDeclaredField("metadata");
            fieldMetadata.setAccessible(true);
            fieldMetadata.set(classDestinoNuevo,
                    obtenerMetadataExito(classDestinoNuevo.getClass().getDeclaredField("metadata").getType()));

            Field fieldData = classDestinoNuevo.getClass().getDeclaredField("data");
            fieldData.setAccessible(true);
            fieldData.set(classDestinoNuevo, data);

            return classDestinoNuevo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @SneakyThrows
    private static <T> T obtenerContenido(Class<T> classResponse) {
        try {
            T classDestinoNuevo = classResponse.newInstance();

            Field fieldMetadata = classDestinoNuevo.getClass().getDeclaredField("metadata");
            fieldMetadata.setAccessible(true);
            fieldMetadata.set(classDestinoNuevo,
                    obtenerMetadataExito(classDestinoNuevo.getClass().getDeclaredField("metadata").getType()));

            return classDestinoNuevo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @SneakyThrows
    private static <T> T obtenerMetadataExito(Class<T> classResponse) {
        try {
            T classDestinoNuevo = classResponse.newInstance();

            Field fieldStatus = classDestinoNuevo.getClass().getDeclaredField("status");
            fieldStatus.setAccessible(true);
            fieldStatus.set(classDestinoNuevo, HttpStatus.OK.value());

            Field fieldMessage = classDestinoNuevo.getClass().getDeclaredField("message");
            fieldMessage.setAccessible(true);
            fieldMessage.set(classDestinoNuevo, "El proceso fue exitoso.");

            return classDestinoNuevo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static String procesarRespuestaNoAutorizado(HttpServletResponse response) throws IOException {
        log.error("Credenciales no autorizadas.");
        ApiDataResponse401 apiDataResponse401 = new ApiDataResponse401();
        apiDataResponse401.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiDataResponse401.setMessage("Acceso no autorizado.");

        ApiResponse401 errorResponse = new ApiResponse401();
        errorResponse.setMetadata(apiDataResponse401);

        String jsonString = new ObjectMapper().writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(jsonString);

        return jsonString;
    }

    public static void procesarRespuestaProhibido(HttpServletResponse response) throws IOException {
        log.error("Acceso denegado al recurso solicitado.");

        ApiDataResponse403 apiDataResponse403 = new ApiDataResponse403();
        apiDataResponse403.setStatus(HttpStatus.FORBIDDEN.value());
        apiDataResponse403.setMessage("No tiene permiso para acceder al recurso solicitado.");

        ApiResponse403 errorResponse = new ApiResponse403();
        errorResponse.setMetadata(apiDataResponse403);

        String jsonString = new ObjectMapper().writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(jsonString);

    }

    public static String maskKey(String key) {
        if (key == null) return "null";
        return key.length() > 4 ? key.substring(0, Math.min(10, key.length())) + "*******" : key;
    }

    public static List<String> maskAllowed(List<String> allowed) {
        if (allowed == null) return Collections.emptyList();
        return allowed.stream()
                .map(ResponseUtil::maskKey)
                .collect(Collectors.toList());
    }

    public static String smallUrl(String url) {
        if (url == null) return "";
        int idx = url.indexOf('?');
        return idx > -1 ? url.substring(0, idx) : url;
    }

}
