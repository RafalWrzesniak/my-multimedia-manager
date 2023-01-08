package wrzesniak.rafal.my.multimedia.manager.web.imdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration("imdbProperties")
@ConfigurationProperties(prefix = "application.imdb")
public class ImdbConfiguration {

    private String url;
    private String urlPl;
    private String apiKey;
    private String notFound;
    private String imageSize;
    private String wikipedia;
    private Map<String, String> api;

    public String getMovieApi() {
        return api.get("movie");
    }

    public String getActorApi() {
        return api.get("actor");
    }

    public String getSearchApi() {
        return api.get("search");
    }

    public String getResizeApi() {
        return api.get("resize");
    }



}
