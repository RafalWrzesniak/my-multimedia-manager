package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import dev.failsafe.RetryPolicy;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

class FilmwebMovieCreatorTest {

    private final WebOperations webOperations = new WebOperations(null, null);
    private final FilmwebMovieCreator filmwebMovieCreator = new FilmwebMovieCreator(webOperations, Mockito.mock(RetryPolicy.class));

    private final static String MOVIE_URL = "https://www.filmweb.pl/film/Most+szpieg%C3%B3w-2015-728144";

    private Document document;

    @BeforeEach
    void setUp() throws IOException {
        this.document = webOperations.parseUrl(toURL(MOVIE_URL));
    }

    @Test
    void parseTitle() {
        String title = filmwebMovieCreator.parseTitle(document);
        assertEquals("Bridge of Spies", title);
    }

    @Test
    void parsePolishTitle() {
        String polishTitle = filmwebMovieCreator.parsePolishTitle(document);
        assertEquals("Most szpiegów", polishTitle);
    }

    @Test
    void parseReleaseDate() {
        LocalDate releaseDate = filmwebMovieCreator.parseReleaseDate(document);
        assertEquals(LocalDate.of(2015, 10, 4), releaseDate);
    }

    @Test
    void parseDuration() {
        int duration = filmwebMovieCreator.parseDuration(document);
        assertEquals(141, duration);
    }

    @Test
    void parseRating() {
        double rating = filmwebMovieCreator.parseRating(document);
        assertTrue(rating > 6.0);
        assertTrue(rating < 9.0);
    }

    @Test
    void parseRatingCount() {
        int ratingCount = filmwebMovieCreator.parseRatingCount(document);
        assertTrue(ratingCount > 120000);
    }

    @Test
    void parseDescription() {
        String description = filmwebMovieCreator.parseDescription(document);
        assertEquals("Amerykański prawnik, broniąc w sądzie sowieckiego szpiega, ratuje go przed karą śmierci. Wkrótce pojawia się możliwość wymiany więźnia na pilota USA zestrzelonego nad terytorium ZSRR.", description);
    }

    @Test
    void parseGenres() {
        Set<String> genres = filmwebMovieCreator.parseGenres(document);
        assertArrayEquals(List.of("Dramat", "Thriller").toArray() , genres.toArray());
    }

    @Test
    void parseCountries() {
        Set<String> countries = filmwebMovieCreator.parseCountries(document);
        assertArrayEquals(List.of("USA", "Indie", "Niemcy").toArray() , countries.toArray());
    }

    @Test
    void parseImage() {
        String imageUrl = filmwebMovieCreator.parseImage(document);
        assertEquals("https://fwcdn.pl/fpo/81/44/728144/7722526.3.jpg", imageUrl);
    }
}