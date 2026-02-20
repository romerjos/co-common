package pe.gob.hospitalcayetano.cocommon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ms")
@Data
public class MsConfigProperties {
    private String key;
    private List<String> allowed = new ArrayList<>();

}
