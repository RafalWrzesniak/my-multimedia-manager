package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.SeriesInfo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb.FilmwebMovieUrl;
import wrzesniak.rafal.my.multimedia.manager.util.StringFunctions;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.firstNotEmpty;


@Service
@Validated
@RequiredArgsConstructor
public class FilmwebMovieCreator {

    private final static String ITEMPROP = "itemprop";
    private final static String CLASS = "class";
    private final static String HREF = "href";

    private final WebOperations webOperations;
    private final RetryPolicy<Object> retryPolicy;

    @SneakyThrows
    public MovieDynamo createMovieFromUrl(@FilmwebMovieUrl URL filmwebUrl) {
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
                .seriesInfo(parseSeriesInfo(document))
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
        try {
            return LocalDate.parse(document.getElementsByAttributeValue(ITEMPROP, "datePublished").first().attr("content"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch(DateTimeParseException e) {
            String foundStringDate = document.getElementsByAttributeValue(ITEMPROP, "datePublished").text();
            Locale.setDefault(Locale.of("pl", "PL"));
            return LocalDate.parse(foundStringDate.substring(0, StringFunctions.findLastDigitIndex(foundStringDate)+1), DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        }
    }
    
    int parseDuration(Document document) {
        return Integer.parseInt(document.getElementsByAttributeValue(ITEMPROP, "timeRequired").first().attr("data-duration"));
    }
    
    double parseRating(Document document) {
        try {
            return Double.parseDouble(document.getElementsByAttributeValue(CLASS, "filmRating__rateValue").first().text().replaceAll(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    int parseRatingCount(Document document) {
        try {
            return Integer.parseInt(document.getElementsByAttributeValue(CLASS, "filmRating__count").first().text().replaceAll(" ", "").replaceAll("[a-z]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    String parseDescription(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "description").first().text();
    }

    Set<String> parseGenres(Document document) {
        Set<String> filmGenres = parseSetOfTextHrefAttributes(document, "/ranking/film/genre");
        return filmGenres.isEmpty() ? parseSetOfTextHrefAttributes(document, "/ranking/serial/genre") : filmGenres;
    }

    Set<String> parseCountries(Document document) {
        Set<String> filmGenres = parseSetOfTextHrefAttributes(document, "/ranking/film/country");
        return filmGenres.isEmpty() ? parseSetOfTextHrefAttributes(document, "/ranking/serial/country") : filmGenres;
    }

    private Set<String> parseSetOfTextHrefAttributes(Document document, String href) {
        return document.getElementsByAttributeValueContaining(HREF, href).stream().map(Element::text).collect(Collectors.toSet());
    }

    public String parseImage(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "image").first().attr("content");
    }

    @SneakyThrows
    public SeriesInfo parseSeriesInfo(Document document) {
        Element sourceElement = document.getElementsByAttributeValue("class", "source").first();
        if(sourceElement == null) {
            return null;
        }
        String parsed = sourceElement.firstChild().toString();
        String formattedInfo = "{" + parsed
                .substring(parsed.indexOf("\"seasonsCount"))
                .replaceAll(";window\\.IRI\\.setSource\\(", "")
                .replaceAll(",", ":")
                .replaceAll("}\\)", ", ")
                .replaceAll("\\);", "}")
                .replaceAll(":\\{count", "");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(formattedInfo, SeriesInfo.class);
    }

}
