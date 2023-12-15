package wrzesniak.rafal.my.multimedia.manager.web.filmweb;

import dev.failsafe.RetryPolicy;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Profile("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class FilmwebServiceTest {

    @Autowired
    private FilmwebConfiguration filmwebConfiguration;
    @Autowired
    private RetryPolicy<Object> retryPolicy;
    @Autowired
    private WebOperations webOperations;

    @Autowired
    private FilmwebService filmwebService;

    private static final String FILMWEB_URL = "https://www.filmweb.pl";
    private static final String MATRIX_PART_URL = "/film/Matrix-1999-628";
    private static final String MATRIX_URL = FILMWEB_URL + MATRIX_PART_URL;
    private static final String PIRATES_URL = "https://www.filmweb.pl/film/Piraci+z+Karaib%C3%B3w%3A+Kl%C4%85twa+Czarnej+Per%C5%82y-2003-37309";
    private static final String PIRATES_TITLE = "Piraci z Karaibów: Klątwa Czarnej Perły";

    @Test
    void shouldBuildFullMovieUrlFormPart() {
        // when
        URL result = filmwebService.createFilmwebUrlFromPart(MATRIX_PART_URL);

        // then
        assertEquals(MATRIX_URL, result.toString());
    }

    @SneakyThrows
    @Test
    void shouldFindTitleForUrl() {
        // when
        String titleResult = filmwebService.findTitleFromUrl(new URL(MATRIX_URL));

        // then
        assertEquals("Matrix (1999)", titleResult);
    }

    @SneakyThrows
    @Test
    void shouldFindLongTitleForUrl() {
        // when
        String titleResult = filmwebService.findTitleFromUrl(new URL(PIRATES_URL));

        // then
        assertEquals(PIRATES_TITLE + " (2003)", titleResult);
    }

}
