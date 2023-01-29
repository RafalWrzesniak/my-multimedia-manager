package wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration("lubimyCzytacProperties")
@ConfigurationProperties(prefix = "application.lubimy-czytac")
public class LubimyCzytacConfiguration {

    private String defaultDescription;
    private String unknownPublisher;
    private Map<String, String> parsing;

}
