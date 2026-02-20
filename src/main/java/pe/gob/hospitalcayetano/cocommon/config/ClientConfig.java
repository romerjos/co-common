
package pe.gob.hospitalcayetano.cocommon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pe.gob.hospitalcayetano.cocommon.interceptor.ClientHttpInterceptor;

import java.util.Collections;

@Configuration
public class ClientConfig {

    @Bean
    public RestTemplate restTemplateConfigurer() {
        final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new
                SimpleClientHttpRequestFactory()));

        restTemplate.setInterceptors(Collections.singletonList(new ClientHttpInterceptor()));
        return restTemplate;
    }

}
