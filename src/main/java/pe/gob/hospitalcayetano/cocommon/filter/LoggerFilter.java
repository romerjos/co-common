package pe.gob.hospitalcayetano.cocommon.filter;

import jakarta.servlet.ServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pe.gob.hospitalcayetano.cocommon.servletloggin.CacheBodyHttpServletRequest;
import pe.gob.hospitalcayetano.cocommon.util.InformacionRequestUtilitario;
import pe.gob.hospitalcayetano.cocommon.util.LoggerUtil;
import pe.gob.hospitalcayetano.cocommon.util.ResponseUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@Slf4j
public class LoggerFilter extends OncePerRequestFilter {

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURL().toString().contains("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        long tiempoInicio = System.currentTimeMillis();

        LoggerUtil.imprimirInformacionEndpoint((jakarta.servlet.http.HttpServletRequest) request);

        if (InformacionRequestUtilitario.validarIpEnListaNegra(InformacionRequestUtilitario.obtenerRequestIP((jakarta.servlet.http.HttpServletRequest) request))) {
            // Al usar jakarta.servlet, ya no es necesario el casteo forzado a jakarta...
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

            ResponseUtil.procesarRespuestaNoAutorizado((jakarta.servlet.http.HttpServletResponse) wrappedResponse);

            wrappedResponse.copyBodyToResponse();
            return;
        }

        CacheBodyHttpServletRequest wrappedRequest = new CacheBodyHttpServletRequest((jakarta.servlet.http.HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        LoggerUtil.imprimirRequestBody(wrappedRequest, (jakarta.servlet.http.HttpServletRequest) request);

        filterChain.doFilter((ServletRequest) wrappedRequest, wrappedResponse);

        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        LoggerUtil.imprimirResponseBody(wrappedResponse, (jakarta.servlet.http.HttpServletResponse) response);

        LoggerUtil.imprimirInformacionFin((jakarta.servlet.http.HttpServletRequest) request, (jakarta.servlet.http.HttpServletResponse) response, tiempoTranscurrido);

        wrappedResponse.copyBodyToResponse();
    }
}