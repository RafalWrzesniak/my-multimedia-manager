package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.slash;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmwebService {

    private static final String TITLE = "title";

    private final WebOperations webOperations;
    private final RetryPolicy<Object> retryPolicy;
    private final FilmwebConfiguration filmwebConfiguration;

    public URL createFilmwebUrlFromPart(String urlPart) {
        URL url = filmwebConfiguration.getUrl();
        return toURL(url + urlPart);
    }

    public String findTitleFromUrl(URL filmwebMovieUrl) {
        log.info("Searching for title in url {}", filmwebMovieUrl);
        AtomicReference<String> foundTitle = new AtomicReference<>();
        Failsafe.with(retryPolicy).run(() -> {
            Document parsedUrl = webOperations.parseUrl(filmwebMovieUrl);
            foundTitle.set(parsedUrl.getElementsByTag(TITLE).text());
        });
        log.info("Found title: {}", foundTitle.get());
        return foundTitle.get().substring(0, foundTitle.get().indexOf(" - Filmweb"));
    }

}
