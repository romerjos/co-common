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

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Slf4j
public final class ResponseUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ResponseUtil() {
    }

    public static <T> ResponseEntity<T> obtenerResultado(Object data, Class<T> classResponse) {
        return ResponseEntity.ok(obtenerContenido(data, classResponse));
    }

    public static <T> ResponseEntity<T> obtenerResultado(Class<T> classResponse) {
        return ResponseEntity.ok(obtenerContenido(classResponse));
    }

    @SneakyThrows
    private static <T> T obtenerContenido(Object data, Class<T> classResponse) {
        try {
            T instance = classResponse.getDeclaredConstructor().newInstance();

            Field metadataField = instance.getClass().getDeclaredField("metadata");
            metadataField.setAccessible(true);
            metadataField.set(instance,
                    obtenerMetadataExito(metadataField.getType()));

            Field dataField = instance.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            dataField.set(instance, data);

            return instance;
        } catch (Exception e) {
            log.error("Error creando contenido de respuesta", e);
            throw e;
        }
    }

    @SneakyThrows
    private static <T> T obtenerContenido(Class<T> classResponse) {
        try {
            T instance = classResponse.getDeclaredConstructor().newInstance();

            Field metadataField = instance.getClass().getDeclaredField("metadata");
            metadataField.setAccessible(true);
            metadataField.set(instance,
                    obtenerMetadataExito(metadataField.getType()));

            return instance;
        } catch (Exception e) {
            log.error("Error creando contenido sin data", e);
            throw e;
        }
    }

    @SneakyThrows
    private static <T> T obtenerMetadataExito(Class<T> metadataClass) {
        try {
            T metadata = metadataClass.getDeclaredConstructor().newInstance();

            Field statusField = metadata.getClass().getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(metadata, HttpStatus.OK.value());

            Field messageField = metadata.getClass().getDeclaredField("message");
            messageField.setAccessible(true);
            messageField.set(metadata, "El proceso fue exitoso.");

            return metadata;
        } catch (Exception e) {
            log.error("Error creando metadata de éxito", e);
            throw e;
        }
    }

    public static String procesarRespuestaNoAutorizado(HttpServletResponse response) throws IOException {
        log.error("Credenciales no autorizadas.");

        ApiDataResponse401 metadata = new ApiDataResponse401();
        metadata.setStatus(HttpStatus.UNAUTHORIZED.value());
        metadata.setMessage("Acceso no autorizado.");

        ApiResponse401 errorResponse = new ApiResponse401();
        errorResponse.setMetadata(metadata);

        String json = OBJECT_MAPPER.writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(json);

        return json;
    }

    public static void procesarRespuestaProhibido(HttpServletResponse response) throws IOException {
        log.error("Acceso denegado al recurso solicitado.");

        ApiDataResponse403 metadata = new ApiDataResponse403();
        metadata.setStatus(HttpStatus.FORBIDDEN.value());
        metadata.setMessage("No tiene permiso para acceder al recurso solicitado.");

        ApiResponse403 errorResponse = new ApiResponse403();
        errorResponse.setMetadata(metadata);

        String json = OBJECT_MAPPER.writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(json);
    }

    public static String maskKey(String key) {
        if (key == null) return "null";
        return key.length() > 4
                ? key.substring(0, Math.min(10, key.length())) + "*******"
                : key;
    }

    public static List<String> maskAllowed(List<String> allowed) {
        if (allowed == null) return Collections.emptyList();
        return allowed.stream()
                .map(ResponseUtil::maskKey)
                .toList(); // Java 21 más limpio
    }

    public static String smallUrl(String url) {
        if (url == null) return "";
        int idx = url.indexOf('?');
        return idx > -1 ? url.substring(0, idx) : url;
    }
}