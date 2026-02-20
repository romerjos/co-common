package pe.gob.hospitalcayetano.cocommon.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import pe.gob.hospitalcayetano.cocommon.util.ConstanteUtil;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
@Slf4j
public class ClientHttpInterceptor implements ClientHttpRequestInterceptor {

    @Value("${ms.key:}")
    private String msKey;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(ConstanteUtil.CLAVE_KEY, msKey);
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("=== >> URI: {} - Método: {}, Headers: {}, Request body: {}",
                request.getURI(), request.getMethod(), request.getHeaders(), new String(body, "UTF-8"));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info("<< === Status code: {} - Status text: {}, Headers: {}, Response body: {}",
                response.getStatusCode(), response.getStatusText(), response.getHeaders(), StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
    }
}
