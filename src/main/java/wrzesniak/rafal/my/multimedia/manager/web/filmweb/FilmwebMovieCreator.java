package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.firstNotEmpty;


@Service
@RequiredArgsConstructor
public class FilmwebMovieCreator {

    private final static String ITEMPROP = "itemprop";
    private final static String CLASS = "class";
    private final static String HREF = "href";

    private final WebOperations webOperations;
    private final RetryPolicy<Object> retryPolicy;

    @SneakyThrows
    public MovieDynamo createMovieFromUrl(URL filmwebUrl) {
        AtomicReference<Document> parsedUrlAtomic = new AtomicReference<>();
        Failsafe.with(retryPolicy)
                .run(() -> parsedUrlAtomic.set(webOperations.parseUrl(filmwebUrl)));
        Document document = parsedUrlAtomic.get();

        return MovieDynamo.builder()
                .title(firstNotEmpty(parseTitle(document), parsePolishTitle(document)))
                .filmwebUrl(filmwebUrl)
                .polishTitle(parsePolishTitle(document))
                .releaseDate(parseReleaseDate(document))
                .runtimeMins(parseDuration(document))
                .rating(BigDecimal.valueOf(parseRating(document)))
                .ratingVotes(parseRatingCount(document))
                .genreList(parseGenres(document))
                .countryList(parseCountries(document))
                .plotLocal(parseDescription(document))
                .webImageUrl(parseImage(document))
                .createdOn(LocalDateTime.now())
                .build();
    }

    String parseTitle(Document document) {
        Element element = document.getElementsByAttributeValue("class", "filmCoverSection__originalTitle").first();
        return Objects.isNull(element) ? "" : element.text();
    }

    String parsePolishTitle(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "name").first().text();
    }
    
    LocalDate parseReleaseDate(Document document) {
        return LocalDate.parse(document.getElementsByAttributeValue(ITEMPROP, "datePublished").first().attr("content"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    
    int parseDuration(Document document) {
        return Integer.parseInt(document.getElementsByAttributeValue(ITEMPROP, "timeRequired").first().attr("data-duration"));
    }
    
    double parseRating(Document document) {
        return Double.parseDouble(document.getElementsByAttributeValue(CLASS, "filmRating__rateValue").first().text().replaceAll(",", "."));
    }

    int parseRatingCount(Document document) {
        return Integer.parseInt(document.getElementsByAttributeValue(CLASS, "filmRating__count").first().text().replaceAll(" ", "").replaceAll("[a-z]", ""));
    }

    String parseDescription(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "description").first().text();
    }

    Set<String> parseGenres(Document document) {
        return document.getElementsByAttributeValueContaining(HREF, "/ranking/film/genre").stream().map(Element::text).collect(Collectors.toSet());
    }

    Set<String> parseCountries(Document document) {
        return document.getElementsByAttributeValueContaining(HREF, "/ranking/film/country").stream().map(Element::text).collect(Collectors.toSet());
    }

    public String parseImage(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "image").first().attr("content");
    }

}
