package pe.gob.hospitalcayetano.cocommon.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pe.gob.hospitalcayetano.cocommon.model.*;
import pe.gob.hospitalcayetano.cocommon.service.EmailService;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@ControllerAdvice
@RefreshScope
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Value("${notificacion.error.lista.destinatarios:alexolc95@gmail.com}")
    private List<String> listaTo;

    @Value("${notificacion.error.enable:true}")
    private Boolean enable;

    @Value("${spring.profiles.active:-}")
    private String environment;

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<ApiResponse401> handlerErrorUnauthorized(HttpServletRequest req, Exception ex) {
        ApiDataResponse401 apiDataResponse401 = new ApiDataResponse401();
        apiDataResponse401.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiDataResponse401.setMessage("Acceso no autorizado.");

        log.info("FeignException.Unauthorized - {}", apiDataResponse401.getMessage());

        ApiResponse401 response = new ApiResponse401();
        response.setMetadata(apiDataResponse401);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(FeignException.Forbidden.class)
    public ResponseEntity<ApiResponse403> handlerFeignForbidden(HttpServletRequest req, FeignException ex) {
        ApiDataResponse403 apiDataResponse403 = new ApiDataResponse403();
        apiDataResponse403.setStatus(HttpStatus.FORBIDDEN.value());
        apiDataResponse403.setMessage("No tiene permiso para acceder al recurso solicitado.");

        log.warn("FeignException.Forbidden - {}", ex.contentUTF8());

        ApiResponse403 response = new ApiResponse403();
        response.setMetadata(apiDataResponse403);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException e) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(e.contentUTF8());

            Object responseBody = mapper.convertValue(root, Object.class);

            return ResponseEntity
                    .status(e.status())
                    .body(responseBody);

        } catch (Exception ex) {
            ApiDataResponseFeign metadata = new ApiDataResponseFeign();
            metadata.setStatus(e.status());
            metadata.setMessage(e.contentUTF8());

            ApiResponseFeign fallback = new ApiResponseFeign();
            fallback.setMetadata(metadata);

            return ResponseEntity.status(e.status()).body(fallback);
        }
    }

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<ApiResponse400> handlerError400(HttpServletRequest req, Exception ex) {

        ApiDataResponse400 apiDataResponse400 = new ApiDataResponse400();
        apiDataResponse400.setStatus(HttpStatus.BAD_REQUEST.value());

        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            apiDataResponse400.setMessage(ex.getMessage());
        } else {
            apiDataResponse400.setMessage("Los datos ingresados son inválidos.");
        }

        log.info("Exception400 - {}", apiDataResponse400.getMessage());

        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof Exception400 exception400) {
            if (exception400.getCodigoError() != null && !exception400.getCodigoError().isEmpty()) {
                headers.add("Code-Error", exception400.getCodigoError());
            }
        }

        ApiResponse400 response = new ApiResponse400();
        response.setMetadata(apiDataResponse400);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, headers, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<ApiResponse401> exception401(HttpServletRequest req, Exception ex) {
        ApiDataResponse401 apiDataResponse401 = new ApiDataResponse401();
        apiDataResponse401.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiDataResponse401.setMessage("Acceso no autorizado.");

        log.info("Exception401 - {}", apiDataResponse401.getMessage());

        ApiResponse401 response = new ApiResponse401();
        response.setMetadata(apiDataResponse401);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<ApiResponse403> handlerError403(HttpServletRequest req, Exception ex) {
        ApiDataResponse403 apiDataResponse403 = new ApiDataResponse403();
        apiDataResponse403.setStatus(HttpStatus.FORBIDDEN.value());

        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            apiDataResponse403.setMessage(ex.getMessage());
        } else {
            apiDataResponse403.setMessage("Acceso denegado.");
        }

        log.info("Exception403 - {}", apiDataResponse403.getMessage());

        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof Exception403 exception403) {
            if (exception403.getCodigoError() != null && !exception403.getCodigoError().isEmpty()) {
                headers.add("Code-Error", exception403.getCodigoError());
            }
        }

        ApiResponse403 response = new ApiResponse403();
        response.setMetadata(apiDataResponse403);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, headers, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<ApiResponse404> handlerError404(HttpServletRequest req, Exception ex) {
        ApiDataResponse404 apiDataResponse404 = new ApiDataResponse404();
        apiDataResponse404.setStatus(HttpStatus.NOT_FOUND.value());

        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            apiDataResponse404.setMessage(ex.getMessage());
        } else {
            apiDataResponse404.setMessage("No se encontró contenido.");
        }

        log.info("Exception404 - {}", apiDataResponse404.getMessage());

        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof Exception404 exception404) {
            if (exception404.getCodigoError() != null && !exception404.getCodigoError().isEmpty()) {
                headers.add("Code-Error", exception404.getCodigoError());
            }
        }

        ApiResponse404 response = new ApiResponse404();
        response.setMetadata(apiDataResponse404);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @ExceptionHandler(Exception409.class)
    public ResponseEntity<ApiResponse409> handlerError409(HttpServletRequest req, Exception ex) {
        ApiDataResponse409 apiDataResponse409 = new ApiDataResponse409();
        apiDataResponse409.setStatus(HttpStatus.CONFLICT.value());

        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            apiDataResponse409.setMessage(ex.getMessage());
        } else {
            apiDataResponse409.setMessage("El recurso ya existe.");
        }

        log.info("Exception409 - {}", apiDataResponse409.getMessage());

        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof Exception409 exception409) {
            if (exception409.getCodigoError() != null && !exception409.getCodigoError().isEmpty()) {
                headers.add("Code-Error", exception409.getCodigoError());
            }
        }

        ApiResponse409 response = new ApiResponse409();
        response.setMetadata(apiDataResponse409);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, headers, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse500> handlerGenericError(HttpServletRequest req, Exception ex) {
        log.error(ex.getMessage(), ex);

        emailService.envioMensajePlano(listaTo, "Error " + "(" + environment + ") " + ex.getMessage(), getStringFromError(ex), enable);

        ApiDataResponse500 apiDataResponse500 = new ApiDataResponse500();
        apiDataResponse500.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiDataResponse500.setMessage("Ocurrió un error no esperado, por favor comuníquese con el área de soporte.");

        ApiResponse500 response = new ApiResponse500();
        response.setMetadata(apiDataResponse500);

        httpServletRequest.setAttribute("exception", getStringFromError(ex));

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    public static String getStringFromError(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

}
