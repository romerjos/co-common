package pe.gob.hospitalcayetano.cocommon.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pe.gob.hospitalcayetano.cocommon.interceptor.MsKeyValidationInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MsKeyValidationInterceptor msKeyValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(msKeyValidationInterceptor)
                .addPathPatterns("/**");
    }
}
