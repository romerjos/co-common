package pe.gob.hospitalcayetano.cocommon.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.gob.hospitalcayetano.cocommon.util.ConstanteUtil;

@Component
@Slf4j
public class FeignMsKeyInterceptor implements RequestInterceptor {

    @Value("${ms.key:}")
    private String msKey;

    @Override
    public void apply(RequestTemplate template) {
        template.header(ConstanteUtil.CLAVE_KEY, msKey);
    }

}
