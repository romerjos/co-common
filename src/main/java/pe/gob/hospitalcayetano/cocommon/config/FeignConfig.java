package pe.gob.hospitalcayetano.cocommon.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.gob.hospitalcayetano.cocommon.logger.CustomFeignLogger;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Logger logger() {
        return new CustomFeignLogger();
    }

}
