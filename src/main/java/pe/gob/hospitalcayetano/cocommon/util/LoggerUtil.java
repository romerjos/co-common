package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pe.gob.hospitalcayetano.cocommon.servletloggin.CacheBodyHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggerUtil {

    private static List<String> listaDataSensible;

    private static List<String> listaDataExtensa;

    private LoggerUtil() {}

    @Value("${lista.data.sensible:#{T(java.util.Collections).emptyList()}}")
    public void setListaDataSensible(List<String> listaDS) {
        listaDataSensible = listaDS;
    }

    @Value("${lista.data.extensa:#{T(java.util.Collections).emptyList()}}")
    public void setListaDataExtensa(List<String> listaDE) {
        listaDataExtensa = listaDE;
    }

    public static String limpiarEspciosBlancoYSantoLinea(String json) {
        String jsonATratar = json.replaceAll("(\n|\r)", "");
        boolean quoted = false;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < jsonATratar.length(); i++) {
            char c = jsonATratar.charAt(i);

            if (c == '\"') quoted = !quoted;

            if (quoted || !Character.isWhitespace(c)) builder.append(c);
        }

        return builder.toString();
    }

    public static String ocultarDataSensibleQueryParam(String data) {
        if (listaDataSensible == null || listaDataSensible.isEmpty()) return data;

        String dataSensibleRegex = listaDataSensible.stream()
                .map(field -> "(?<=" + field + "=)([^&]*)")
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        return data.replaceAll(dataSensibleRegex, "*****");
    }

    public static String ocultarDataSensible(String data) {
        String dataSensibleRegex = listaDataSensible.stream()
                .map(field -> "(?<=\\\"" + field + "\\\":\\\")[^\"]+?(?=\\\")")
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        return data.replaceAll(dataSensibleRegex, "*****");
    }

    public static String reducirDataExtensa(String data) {
        String dataExtensaRegex = listaDataExtensa.stream()
                .map(field -> "(?<=\\\"" + field + "\\\":\\\")[^\"]+?(?=\\\")")
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        Pattern pattern = Pattern.compile(dataExtensaRegex);
        Matcher matcher = pattern.matcher(data);

        StringBuilder updatedRequestBody = new StringBuilder();

        while (matcher.find()) {
            String sensitiveValue = matcher.group();
            String truncatedValue = sensitiveValue.length() > 10
                    ? sensitiveValue.substring(0, 10) + "..."
                    : sensitiveValue;
            matcher.appendReplacement(updatedRequestBody, truncatedValue);
        }

        matcher.appendTail(updatedRequestBody);

        return updatedRequestBody.toString();
    }

    private static String obtenerDataString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) return null;
        try {
            return new String(buf, charsetName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String imprimirRequestBody(CacheBodyHttpServletRequest wrappedRequest, HttpServletRequest request) throws IOException {
        //String requestBodyString = obtenerDataString(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());
        String requestBodyString = wrappedRequest.getReader().lines().collect(Collectors.joining());
        if (requestBodyString == null) return "";

        StringBuilder requestBody = new StringBuilder();
        requestBody.append(LoggerUtil.limpiarEspciosBlancoYSantoLinea(requestBodyString));

        if (listaDataSensible != null && !listaDataSensible.isEmpty()) {
            String informacionOcultada = LoggerUtil.ocultarDataSensible(requestBody.toString());

            requestBody.setLength(0);
            requestBody.append(informacionOcultada);
        }

        if (listaDataExtensa != null && !listaDataExtensa.isEmpty()) {
            String informacionRecortada = LoggerUtil.reducirDataExtensa(requestBody.toString());

            requestBody.setLength(0);
            requestBody.append(informacionRecortada);
        }

        log.info("Request body:\n" + requestBody);
        return requestBody.toString();
    }

    public static String imprimirResponseBody(ContentCachingResponseWrapper wrappedResponse, HttpServletResponse response) {
        String responseBodyString = obtenerDataString(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
        if (responseBodyString == null) return "";

        boolean esPdf = responseBodyString.startsWith("%PDF");
        boolean contieneBinario = esContenidoBinario(wrappedResponse);

        if (esPdf || contieneBinario) {
            int maxLen = Math.min(20, responseBodyString.length());
            String preview = responseBodyString.substring(0, maxLen).replaceAll("[\n\r]", "");
            log.info("Response body:\n[binario detectado] {}...", preview);
            return preview;
        }

        StringBuilder responseBody = new StringBuilder();
        responseBody.append(limpiarEspciosBlancoYSantoLinea(responseBodyString));
        String informacionRecortada;

        if (listaDataSensible != null && !listaDataSensible.isEmpty()) {
            informacionRecortada = ocultarDataSensible(responseBody.toString());
            responseBody.setLength(0);
            responseBody.append(informacionRecortada);
        }

        if (listaDataExtensa != null && !listaDataExtensa.isEmpty()) {
            informacionRecortada = reducirDataExtensa(responseBody.toString());
            responseBody.setLength(0);
            responseBody.append(informacionRecortada);
        }

        log.info("Response body:\n" + responseBody);
        return responseBody.toString();
    }

    public static boolean isTieneValoresListaDataSensible() {
        return listaDataSensible != null && !listaDataSensible.isEmpty();
    }

    public static boolean isTieneValoresListaDataExtensa() {
        return listaDataExtensa != null && !listaDataExtensa.isEmpty();
    }

    public static String imprimirInformacionEndpoint(HttpServletRequest request) {
        String info = obtenerInformacionEndpoint(request).toString();
        log.info("==> " + info);
        return info;
    }

    public static String imprimirInformacionFin(HttpServletRequest request, HttpServletResponse response, long tiempoTranscurrido) {
        StringBuilder infoEndpoint = obtenerInformacionEndpoint(request);
        int status = response.getStatus();
        double tiempoSegundos = (double) tiempoTranscurrido / 1000.0;

        String mensaje = "<== " + infoEndpoint + " - response HTTP=" + status + " en " + tiempoSegundos + "s";
        log.info(mensaje);
        return mensaje;
    }

    public static StringBuilder obtenerInformacionEndpoint(HttpServletRequest request) {
        StringBuilder informacionEndpoint = new StringBuilder()
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURL());

        if (request.getQueryString() != null) {
            informacionEndpoint.append("?").append(ocultarDataSensibleQueryParam(request.getQueryString()));
        }

        return informacionEndpoint;
    }

    public static boolean esContenidoBinario(ContentCachingResponseWrapper wrappedResponse) {
        byte[] responseBytes = wrappedResponse.getContentAsByteArray();
        if (responseBytes == null || responseBytes.length < 4) {
            return false;
        }

        boolean esPdf = responseBytes.length > 4 &&
                responseBytes[0] == 0x25 && responseBytes[1] == 0x50 &&
                responseBytes[2] == 0x44 && responseBytes[3] == 0x46;

        boolean esZip = responseBytes.length > 4 &&
                responseBytes[0] == 0x50 && responseBytes[1] == 0x4B &&
                (responseBytes[2] == 0x03 || responseBytes[2] == 0x05 || responseBytes[2] == 0x07) &&
                (responseBytes[3] == 0x04 || responseBytes[3] == 0x06 || responseBytes[3] == 0x08);

        boolean esJpeg = responseBytes.length > 3 &&
                responseBytes[0] == (byte) 0xFF && responseBytes[1] == (byte) 0xD8 &&
                responseBytes[2] == (byte) 0xFF;

        boolean esExcelBin = responseBytes.length > 8 &&
                responseBytes[0] == (byte) 0xD0 && responseBytes[1] == (byte) 0xCF &&
                responseBytes[2] == (byte) 0x11 && responseBytes[3] == (byte) 0xE0;

        boolean esPng = responseBytes.length > 8 &&
                responseBytes[0] == (byte) 0x89 &&
                responseBytes[1] == 0x50 &&
                responseBytes[2] == 0x4E &&
                responseBytes[3] == 0x47;

        return (esPdf || esZip || esJpeg || esExcelBin || esPng);
    }


}
