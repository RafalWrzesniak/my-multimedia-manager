package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.Map;

@Data
@Configuration("filmwebProperties")
@ConfigurationProperties(prefix = "application.filmweb")
public class FilmwebConfiguration {

    private URL url;
    private String search;
    private Map<String, String> match;

}
