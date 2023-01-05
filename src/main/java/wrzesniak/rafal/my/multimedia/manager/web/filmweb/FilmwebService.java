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
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.slash;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmwebService {

    private static final String HREF = "href";
    private static final String TITLE = "title";

    private final WebOperations webOperations;
    private final RetryPolicy<Object> retryPolicy;
    private final FilmwebConfiguration filmwebConfiguration;

    @SneakyThrows
    public URL createFilmwebUrlFromPart(String urlPart) {
        URL url = filmwebConfiguration.getUrl();
        return new URL(url + urlPart);
    }

    @SneakyThrows
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

    public void addFilmwebUrlTo(FilmwebSearchable filmwebSearchable) {
        Failsafe.with(retryPolicy).run(() -> {
            URL filmwebUrl = findUrlFor(filmwebSearchable);
            filmwebSearchable.setFilmwebUrl(filmwebUrl);
        });
    }

    private URL findUrlFor(FilmwebSearchable filmwebSearchable) throws IOException {
        String searchString = filmwebSearchable.getFilmwebSearchString();
        URL query = createFilmwebQueryFrom(searchString);
        String matchCriteria = findMatchCriteriaBasedOnClass(filmwebSearchable);
        URL urlFromQueryUrlByMatch = findUrlFromQueryUrlByMatch(query, matchCriteria);
        log.info("URL found for {} is {}", filmwebSearchable.getFilmwebSearchString(), urlFromQueryUrlByMatch);
        return urlFromQueryUrlByMatch;
    }

    @SneakyThrows
    private URL createFilmwebQueryFrom(String query) {
        URL url = filmwebConfiguration.getUrl();
        String search = filmwebConfiguration.getSearch();
        return new URL(url + slash(search) + URLEncoder.encode(query, UTF_8));
    }

    private String findMatchCriteriaBasedOnClass(FilmwebSearchable filmwebSearchable) {
        String match = filmwebSearchable.getClass().getSimpleName().toLowerCase();
        int dtoIndex = match.indexOf("dto");
        if(dtoIndex > 0) {
            match = match.substring(0, dtoIndex);
        }
        return filmwebConfiguration.getLink().getPrefix().get(match);
    }

    private URL findUrlFromQueryUrlByMatch(URL query, String prefix) throws IOException {
        log.info("Searching for prefix {} in query {}", prefix, query);
        Document parsedUrl = webOperations.parseUrl(query);
        Element foundElementWithUrl = parsedUrl.getElementsByAttributeValueStarting(HREF, prefix).first();
        return foundElementWithUrl != null ? createFilmwebUrlFromPart(foundElementWithUrl.attr(HREF)) : null;
    }

}
