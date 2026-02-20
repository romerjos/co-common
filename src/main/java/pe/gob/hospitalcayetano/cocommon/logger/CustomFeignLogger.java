package pe.gob.hospitalcayetano.cocommon.logger;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pe.gob.hospitalcayetano.cocommon.util.ConstanteUtil;
import pe.gob.hospitalcayetano.cocommon.util.LoggerUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomFeignLogger extends Slf4jLogger {

    @Override
    protected void logRequest(String configKey, Logger.Level logLevel, Request request) {
        FeignRequest feignRequest = new FeignRequest();
        feignRequest.setMethod(request.method());
        feignRequest.setUrl(request.url());

        if (logLevel.ordinal() >= Logger.Level.HEADERS.ordinal()) {
            for (String field : request.headers().keySet()) {
                for (String value : Util.valuesOrEmpty(request.headers(), field)) {
                    if (ConstanteUtil.CLAVE_KEY.equalsIgnoreCase(field)) {
                        String safeKey = (value != null && value.length() > 4)
                                ? value.substring(0, 6) + "****"
                                : "";
                        feignRequest.addHeader(field, safeKey);
                    } else {
                        feignRequest.addHeader(field, value);
                    }
                }
            }

            if (request.body() != null && logLevel.ordinal() >= Logger.Level.FULL.ordinal()) {
                String dataRequest = request.charset() != null ? new String(request.body(), request.charset()) : null;

                if (dataRequest != null) {
                    if (LoggerUtil.isTieneValoresListaDataSensible()) {
                        dataRequest = LoggerUtil.ocultarDataSensible(dataRequest);
                    }

                    if (LoggerUtil.isTieneValoresListaDataExtensa()) {
                        dataRequest = LoggerUtil.reducirDataExtensa(dataRequest);
                    }
                }

                feignRequest.setBody(dataRequest);
            }
        }

        log.info(String.format("%s === >> Request body: %s ", configKey, feignRequest));
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Logger.Level logLevel, Response response, long elapsedTime) throws IOException {
        FeignResponse feignResponse = new FeignResponse();
        int status = response.status();
        feignResponse.setStatus(response.status());
        feignResponse.setReason(response.reason() != null && logLevel.compareTo(Logger.Level.NONE) > 0 ? " " + response.reason() : "");
        feignResponse.setTimeTaken(elapsedTime);

        if (logLevel.ordinal() >= Logger.Level.HEADERS.ordinal()) {
            for (String field : response.headers().keySet()) {
                for (String value : Util.valuesOrEmpty(response.headers(), field)) {
                    feignResponse.addHeader(field, value);
                }
            }

            if (response.body() != null && !(status == 204 || status == 205)) {
                byte[] bodyData = Util.toByteArray(response.body().asInputStream());

                if (logLevel.ordinal() >= Logger.Level.FULL.ordinal() && bodyData.length > 0) {
                    String dataResponse = Util.decodeOrDefault(bodyData, StandardCharsets.UTF_8, null);

                    if (dataResponse != null) {
                        if (LoggerUtil.isTieneValoresListaDataSensible()) {
                            dataResponse = LoggerUtil.ocultarDataSensible(dataResponse);
                        }

                        if (LoggerUtil.isTieneValoresListaDataExtensa()) {
                            dataResponse = LoggerUtil.reducirDataExtensa(dataResponse);
                        }
                    }

                    feignResponse.setBody(dataResponse);
                }

                log.info(String.format("%s << === Response body: %s ", configKey, feignResponse));

                return response.toBuilder().body(bodyData).build();
            } else {
                log.info(String.format("%s << === Response body: %s ", configKey, feignResponse));
            }
        }

        return response;
    }

    @Setter
    private class FeignRequest {
        private String method;
        private String url;
        private List<String> headers;
        private String body;

        public void addHeader(String key, String value) {
            if (headers == null) {
                headers = new ArrayList<>();
            }

            headers.add(String.format("%s: %s", key, value));
        }

        @Override
        public String toString() {
            return String.format("URI: %s, Method: %s, Headers: %s Request body: %s",
                    LoggerUtil.ocultarDataSensibleQueryParam(url), method, headers, body);
        }
    }

    @Setter
    private class FeignResponse {
        private int status;
        private String reason;
        private long timeTaken;
        private List<String> headers;
        private String body;

        public void addHeader(String key, String value) {
            if (headers == null) {
                headers = new ArrayList<>();
            }

            headers.add(String.format("%s: %s", key, value));
        }

        @Override
        public String toString() {
            return String.format("Status code: %s, Reason: %s, Time: %s, Headers: %s Response body: %s",
                    status, reason, timeTaken, headers, body);
        }
    }

}
