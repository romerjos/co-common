package pe.gob.hospitalcayetano.cocommon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pe.gob.hospitalcayetano.cocommon.servletloggin.CacheBodyHttpServletRequest;

import javax.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class LoggerUtil {

    private static final Logger log = LoggerFactory.getLogger(LoggerUtil.class);

    private static List<String> listaDataSensible = Collections.emptyList();
    private static List<String> listaDataExtensa = Collections.emptyList();

    @Value("${lista.data.sensible:}")
    private List<String> listaDSConfig;

    @Value("${lista.data.extensa:}")
    private List<String> listaDEConfig;

    public static boolean isTieneValoresListaDataSensible() {
        return false;
    }

    public static boolean isTieneValoresListaDataExtensa() {
        return false;
    }

    @PostConstruct
    public void init() {
        if (listaDSConfig != null) {
            listaDataSensible = listaDSConfig;
        }
        if (listaDEConfig != null) {
            listaDataExtensa = listaDEConfig;
        }
    }

    public static String limpiarEspaciosBlancoYSaltosLinea(String json) {
        if (json == null) return "";

        String jsonATratar = json.replaceAll("[\\n\\r]", "");
        boolean quoted = false;
        StringBuilder builder = new StringBuilder();

        for (char c : jsonATratar.toCharArray()) {
            if (c == '"') quoted = !quoted;
            if (quoted || !Character.isWhitespace(c)) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public static String ocultarDataSensibleQueryParam(String data) {
        if (data == null || listaDataSensible.isEmpty()) return data;

        String regex = listaDataSensible.stream()
                .map(field -> "(?<=" + field + "=)([^&]*)")
                .collect(Collectors.joining("|"));

        return data.replaceAll(regex, "*****");
    }

    public static String ocultarDataSensible(String data) {
        if (data == null || listaDataSensible.isEmpty()) return data;

        String regex = listaDataSensible.stream()
                .map(field -> "(?<=\\\"" + field + "\\\":\\\")[^\"]+?(?=\\\")")
                .collect(Collectors.joining("|"));

        return data.replaceAll(regex, "*****");
    }

    public static String reducirDataExtensa(String data) {
        if (data == null || listaDataExtensa.isEmpty()) return data;

        String regex = listaDataExtensa.stream()
                .map(field -> "(?<=\\\"" + field + "\\\":\\\")[^\"]+?(?=\\\")")
                .collect(Collectors.joining("|"));

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String value = matcher.group();
            String truncated = value.length() > 10
                    ? value.substring(0, 10) + "..."
                    : value;
            matcher.appendReplacement(sb, truncated);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String obtenerDataString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) return null;
        try {
            Charset charset = charsetName != null
                    ? Charset.forName(charsetName)
                    : Charset.defaultCharset();
            return new String(buf, charset);
        } catch (Exception e) {
            log.error("Error convirtiendo respuesta a String", e);
            return null;
        }
    }

    public static String imprimirRequestBody(CacheBodyHttpServletRequest wrappedRequest,
                                             HttpServletRequest request) throws Exception {

        String body = wrappedRequest.getReader()
                .lines()
                .collect(Collectors.joining());

        if (body == null) return "";

        body = limpiarEspaciosBlancoYSaltosLinea(body);
        body = ocultarDataSensible(body);
        body = reducirDataExtensa(body);

        log.info("Request body: {}", body);
        return body;
    }

    public static String imprimirResponseBody(ContentCachingResponseWrapper wrappedResponse,
                                              HttpServletResponse response) {

        String body = obtenerDataString(
                wrappedResponse.getContentAsByteArray(),
                response.getCharacterEncoding());

        if (body == null) return "";

        if (esContenidoBinario(wrappedResponse)) {
            log.info("Response body: [binario detectado]");
            return "[binario detectado]";
        }

        body = limpiarEspaciosBlancoYSaltosLinea(body);
        body = ocultarDataSensible(body);
        body = reducirDataExtensa(body);

        log.info("Response body: {}", body);
        return body;
    }

    public static boolean esContenidoBinario(ContentCachingResponseWrapper wrappedResponse) {
        byte[] bytes = wrappedResponse.getContentAsByteArray();
        if (bytes == null || bytes.length < 4) return false;

        return (bytes[0] == 0x25 && bytes[1] == 0x50 &&
                bytes[2] == 0x44 && bytes[3] == 0x46); // PDF
    }

    public static String imprimirInformacionEndpoint(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder()
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURL());

        if (request.getQueryString() != null) {
            sb.append("?")
                    .append(ocultarDataSensibleQueryParam(request.getQueryString()));
        }

        String info = sb.toString();
        log.info("==> {}", info);
        return info;
    }

    public static String imprimirInformacionFin(HttpServletRequest request,
                                                HttpServletResponse response,
                                                long tiempoMs) {

        double segundos = tiempoMs / 1000.0;

        String mensaje = "<== " + request.getMethod() +
                " " + request.getRequestURI() +
                " - HTTP=" + response.getStatus() +
                " en " + segundos + "s";

        log.info(mensaje);
        return mensaje;
    }
}