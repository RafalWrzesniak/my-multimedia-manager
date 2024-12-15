package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import dev.failsafe.RetryPolicy;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.SeriesInfo;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

class FilmwebMovieCreatorTest {

    private final WebOperations webOperations = new WebOperations();
    private final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                                                        .handle(IOException.class)
                                                        .withDelay(Duration.ofSeconds(1))
                                                        .withMaxRetries(3)
                                                        .build();

    private final FilmwebMovieCreator filmwebMovieCreator = new FilmwebMovieCreator(webOperations, retryPolicy);

    private final static String BRIDGE_OF_SPIES = "https://www.filmweb.pl/film/Most+szpieg%C3%B3w-2015-728144";
    private final static String KILER = "https://www.filmweb.pl/film/Kiler-1997-529";
    private final static String GAME_OF_THRONES = "https://www.filmweb.pl/serial/Gra+o+tron-2011-476848";


    @SneakyThrows
    private Document parseToDocument(String movieUrl) {
        return webOperations.parseUrl(toURL(movieUrl));
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",Bridge of Spies",
            KILER+",",
            GAME_OF_THRONES+",Game of Thrones"
    })
    void parseTitle(String movieUrl, String expectedTitle) {
        Document document = parseToDocument(movieUrl);
        String title = filmwebMovieCreator.parseTitle(document);
        assertEquals(Optional.ofNullable(expectedTitle).orElse(""), title);
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",Most szpiegów",
            KILER+",Kiler",
            GAME_OF_THRONES+",Gra o tron"
    })
    void parsePolishTitle(String movieUrl, String expectedTitle) {
        Document document = parseToDocument(movieUrl);
        String polishTitle = filmwebMovieCreator.parsePolishTitle(document);
        assertEquals(expectedTitle, polishTitle);
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",2015-10-04",
            KILER+",1997-11-17",
            GAME_OF_THRONES+",2010-12-05"
    })
    void parseReleaseDate(String movieUrl, String expectedReleaseDate) {
        Document document = parseToDocument(movieUrl);
        LocalDate releaseDate = filmwebMovieCreator.parseReleaseDate(document);
        assertEquals(LocalDate.parse(expectedReleaseDate), releaseDate);
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",141",
            KILER+",104",
            GAME_OF_THRONES+",60"
    })
    void parseDuration(String movieUrl, int expectedDuration) {
        Document document = parseToDocument(movieUrl);
        int duration = filmwebMovieCreator.parseDuration(document);
        assertEquals(expectedDuration, duration);
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES,
            KILER,
            GAME_OF_THRONES
    })
    void parseRating(String movieUrl) {
        Document document = parseToDocument(movieUrl);
        double rating = filmwebMovieCreator.parseRating(document);
        assertTrue(rating > 6.0);
        assertTrue(rating < 9.0);
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES+",120000",
            KILER+",460000",
            GAME_OF_THRONES+",390000"
    })
    void parseRatingCount(String movieUrl, int minimalRatingCount) {
        Document document = parseToDocument(movieUrl);
        int ratingCount = filmwebMovieCreator.parseRatingCount(document);
        assertTrue(ratingCount > minimalRatingCount);
        assertTrue(ratingCount < 1_000_000);
    }


    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",Amerykański prawnik broniąc w sądzie sowieckiego szpiega ratuje go przed karą śmierci. Wkrótce pojawia się możliwość wymiany więźnia na pilota USA zestrzelonego nad terytorium ZSRR.",
            KILER+",Jerzy Kiler warszawski taksówkarz przypadkowo zostaje wzięty za płatnego zabójcę i umieszczony w areszcie. Wyciąga go stamtąd boss świata przestępczego który oferuje mu nowe zadanie.",
            GAME_OF_THRONES+",Adaptacja sagi George'a R.R. Martina. W królestwie Westeros walka o władzę spiski oraz zbrodnie są na porządku dziennym."
    })
    void parseDescription(String movieUrl, String expectedDescription) {
        Document document = parseToDocument(movieUrl);
        String description = filmwebMovieCreator.parseDescription(document).replaceAll(",", "");
        assertEquals(expectedDescription, description);
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",Dramat,Thriller,",
            KILER+",Sensacyjny,Komedia,",
            GAME_OF_THRONES+",Dramat,Fantasy,Przygodowy"
    })
    void parseGenres(String movieUrl, String expectedGenre1, String expectedGenre2, String expectedGenre3) {
        String[] expectedGenres = Stream.of(expectedGenre1, expectedGenre2, expectedGenre3)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
        Document document = parseToDocument(movieUrl);
        Set<String> genres = filmwebMovieCreator.parseGenres(document);
        assertArrayEquals(expectedGenres , genres.toArray());
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",USA,Indie,Niemcy",
            KILER+",Polska,,",
            GAME_OF_THRONES+",USA,Wielka Brytania,"
    })
    void parseCountries(String movieUrl, String expectedCountry1, String expectedCountry2, String expectedCountry3) {
        String[] expectedCountries = Stream.of(expectedCountry1, expectedCountry2, expectedCountry3)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
        Document document = parseToDocument(movieUrl);
        Set<String> countries = filmwebMovieCreator.parseCountries(document);
        assertArrayEquals(expectedCountries , countries.toArray());
    }

    @ParameterizedTest
    @CsvSource({
            BRIDGE_OF_SPIES +",https://fwcdn.pl/fpo/81/44/728144/7722526",
            KILER+",https://fwcdn.pl/fpo/05/29/529/6900785",
            GAME_OF_THRONES+",https://fwcdn.pl/fpo/68/48/476848/8145248"
    })
    void parseImage(String movieUrl, String expectedImageUrlStart) {
        Document document = parseToDocument(movieUrl);
        String imageUrl = filmwebMovieCreator.parseImage(document);
        assertTrue(imageUrl.startsWith(expectedImageUrlStart));
    }

    @Test
    @SneakyThrows
    void shouldCreateSeries() {
        Document document = parseToDocument(GAME_OF_THRONES);
        SeriesInfo seriesInfo = filmwebMovieCreator.parseSeriesInfo(document);
        assertEquals(new SeriesInfo(8, 79), seriesInfo);
    }


}