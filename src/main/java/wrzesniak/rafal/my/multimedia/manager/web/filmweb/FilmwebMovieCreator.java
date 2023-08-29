package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.firstNotEmpty;


@Service
@RequiredArgsConstructor
public class FilmwebMovieCreator {

    private final static String ITEMPROP = "itemprop";
    private final static String CLASS = "class";
    private final static String HREF = "href";

    private final WebOperations webOperations;

    @SneakyThrows
    public Movie createMovieFromUrl(URL filmwebUrl) {
        Document document = webOperations.parseUrl(filmwebUrl);
        String director = document.getElementsByAttributeValue(ITEMPROP, "director").first().attr("title");
        String writer = document.getElementsByAttributeValue(ITEMPROP, "creator").first().attr("title");

        return Movie.builder()
                .title(firstNotEmpty(parseTitle(document), parsePolishTitle(document)))
                .filmwebUrl(filmwebUrl)
                .polishTitle(parsePolishTitle(document))
                .releaseDate(parseReleaseDate(document))
                .runtimeMins(parseDuration(document))
                .imDbRating(BigDecimal.valueOf(parseRating(document)))
                .imDbRatingVotes(parseRatingCount(document))
                .genreList(parseGenres(document))
                .countryList(parseCountries(document))
                .plotLocal(parseDescription(document))
                .webImageUrl(parseImage(document))
                .actorList(new HashSet<>())
                .directorList(new HashSet<>())
                .writerList(new HashSet<>())
                .createdOn(LocalDate.now())
                .build();
    }


    @VisibleForTesting
    String parseTitle(Document document) {
        Element element = document.getElementsByAttributeValue("class", "filmCoverSection__originalTitle").first();
        return Objects.isNull(element) ? "" : element.text();
    }

    @VisibleForTesting
    String parsePolishTitle(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "name").first().text();
    }
    
    @VisibleForTesting
    LocalDate parseReleaseDate(Document document) {
        return LocalDate.parse(document.getElementsByAttributeValue(ITEMPROP, "datePublished").first().attr("content"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    
    @VisibleForTesting
    int parseDuration(Document document) {
        return Integer.parseInt(document.getElementsByAttributeValue(ITEMPROP, "timeRequired").first().attr("data-duration"));
    }
    
    @VisibleForTesting
    double parseRating(Document document) {
        return Double.parseDouble(document.getElementsByAttributeValue(CLASS, "filmRating__rateValue").first().text().replaceAll(",", "."));
    }

    @VisibleForTesting
    int parseRatingCount(Document document) {
        return Integer.parseInt(document.getElementsByAttributeValue(CLASS, "filmRating__count").first().text().replaceAll(" ", "").replaceAll("[a-z]", ""));
    }

    @VisibleForTesting
    String parseDescription(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "description").first().text();
    }

    @VisibleForTesting
    Set<String> parseGenres(Document document) {
        return document.getElementsByAttributeValueContaining(HREF, "/ranking/film/genre").stream().map(Element::text).collect(Collectors.toSet());
    }

    @VisibleForTesting
    Set<String> parseCountries(Document document) {
        return document.getElementsByAttributeValueContaining(HREF, "/ranking/film/country").stream().map(Element::text).collect(Collectors.toSet());
    }

    @VisibleForTesting
    String parseImage(Document document) {
        return document.getElementsByAttributeValue(ITEMPROP, "image").first().attr("content");
    }

}
