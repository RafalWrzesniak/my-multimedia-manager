package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorManagementService;
import wrzesniak.rafal.my.multimedia.manager.util.Validators;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebService;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Profile("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class MovieCreatorServiceTest {

    @Autowired
    private Validators validators;
    @Autowired
    private ImdbService imdbService;
    @Autowired
    private WebOperations webOperations;
    @Autowired
    private FilmwebService filmwebService;
    @MockBean
    private ActorManagementService actorManagementService;
    @MockBean
    private MovieRepository movieRepository;

    @Autowired
    private MovieCreatorService movieCreatorService;

    private static final String MATRIX_TITLE = "Matrix";


    @SneakyThrows
    @Test
    void shouldFindAndCreateMatrixMovie() {
        // given
        ArgumentCaptor<Movie> movieParam = ArgumentCaptor.forClass(Movie.class);
        when(movieRepository.save(movieParam.capture())).thenAnswer((invocation) -> movieParam.getValue());
        given(actorManagementService.createActorFromImdbId(any())).willReturn(Optional.empty());

        // when
        Optional<Movie> optionalMovieFromPolishTitle = movieCreatorService.createMovieFromPolishTitle(MATRIX_TITLE);
        Movie createdMovie = optionalMovieFromPolishTitle.orElseThrow();
        createdMovie.setImDbRating(8.7);
        createdMovie.setImDbRatingVotes(1000);

        // then
        assertEquals("The Matrix", createdMovie.getTitle());
        assertEquals(MATRIX_TITLE, createdMovie.getPolishTitle());
        assertEquals(new URL("https://www.filmweb.pl/film/Matrix-1999-628"), createdMovie.getFilmwebUrl());
        assertEquals(LocalDate.of(1999, 3, 31), createdMovie.getReleaseDate());
        assertEquals("tt0133093", createdMovie.getImdbId());
        assertEquals(136, createdMovie.getRuntimeMins());
        assertTrue(createdMovie.getGenreList().containsAll(List.of("Akcja", "Science fiction")));
        assertTrue(createdMovie.getCountryList().containsAll(List.of("USA", "Australia")));
    }

    @SneakyThrows
    @Test
    void shouldReturnEmptyWhenWrongFilmwebUrl() {

        // when
        Optional<Movie> movieFromFilmwebUrl = movieCreatorService.createMovieFromFilmwebUrl(new URL("https://www.bad-url.com"));

        // then
        assertTrue(movieFromFilmwebUrl.isEmpty());
    }

    @SneakyThrows
    @Test
    void shouldCallCreateMovieFromPolishTitleWhenCreatingFromUrl() {
        // given
        ArgumentCaptor<Movie> movieParam = ArgumentCaptor.forClass(Movie.class);
        when(movieRepository.save(movieParam.capture())).thenAnswer((invocation) -> movieParam.getValue());
        given(actorManagementService.createActorFromImdbId(any())).willReturn(Optional.empty());
        MovieCreatorService creatorServiceSpy = Mockito.spy(new MovieCreatorService(validators, imdbService, webOperations, filmwebService, movieRepository, actorManagementService));

        // when
        creatorServiceSpy.createMovieFromFilmwebUrl(new URL("https://www.filmweb.pl/film/Matrix-1999-628"));

        // then
        verify(creatorServiceSpy).createMovieFromPolishTitle("Matrix (1999)");
    }

    @Test
    void shouldCallCreateMovieFromPolishTitleWhenCreatingPath() {
        // given
        ArgumentCaptor<Movie> movieParam = ArgumentCaptor.forClass(Movie.class);
        when(movieRepository.save(movieParam.capture())).thenAnswer((invocation) -> movieParam.getValue());
        given(actorManagementService.createActorFromImdbId(any())).willReturn(Optional.empty());
        MovieCreatorService creatorServiceSpy = Mockito.spy(new MovieCreatorService(validators, imdbService, webOperations, filmwebService, movieRepository, actorManagementService));

        // when
        creatorServiceSpy.createMovieFromLocalFolder(new File("E:\\Rafał\\Filmy\\Matrix\\Matrix.mkv"));

        // then
        verify(creatorServiceSpy).createMovieFromPolishTitle("Matrix");
    }
}