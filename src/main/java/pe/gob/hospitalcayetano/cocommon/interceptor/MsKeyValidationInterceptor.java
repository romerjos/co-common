package pe.gob.hospitalcayetano.cocommon.interceptor;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.gob.hospitalcayetano.cocommon.config.MsConfigProperties;
import pe.gob.hospitalcayetano.cocommon.util.ConstanteUtil;
import pe.gob.hospitalcayetano.cocommon.util.ResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class MsKeyValidationInterceptor implements HandlerInterceptor {

    private final MsConfigProperties msConfig;

    private final Environment environment;

    @Value("${ms.validate-allowed:true}")
    private Boolean validateAllowed;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String[] activeProfiles = environment.getActiveProfiles();

        if (Arrays.asList(activeProfiles).contains("local")) {
            return true;
        }

        if (!validateAllowed) {
            return true;
        }

        if (msConfig.getAllowed() == null || msConfig.getAllowed().isEmpty()) {
            return false;
        }

        String callerKey = request.getHeader(ConstanteUtil.CLAVE_KEY);

        if (!msConfig.getAllowed().contains(callerKey)) {

            ResponseUtil.procesarRespuestaProhibido(response);
            return false;
        }


        return true;
    }

}

