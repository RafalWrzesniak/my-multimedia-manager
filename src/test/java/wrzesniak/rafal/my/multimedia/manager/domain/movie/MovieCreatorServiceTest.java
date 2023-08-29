package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.ActorCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.repository.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.service.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.service.S3Service;
import wrzesniak.rafal.my.multimedia.manager.util.StringFunctions;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebMovieCreator;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import java.math.BigDecimal;
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
    private ImdbService imdbService;
    @Autowired
    private WebOperations webOperations;
    @MockBean
    private ActorCreatorService actorCreatorService;
    @MockBean
    private MovieRepository movieRepository;
    @MockBean
    private S3Service s3Service;
    @MockBean
    private FilmwebMovieCreator filmwebMovieCreator;

    @Autowired
    private MovieCreatorService movieCreatorService;

    private static final String MATRIX_TITLE = "Matrix";


    @SneakyThrows
//    @Test
    void shouldFindAndCreateMatrixMovie() {
        // given
        ArgumentCaptor<Movie> movieParam = ArgumentCaptor.forClass(Movie.class);
        when(movieRepository.save(movieParam.capture())).thenAnswer((invocation) -> movieParam.getValue());
        given(actorCreatorService.createActorFromImdbId(any())).willReturn(Optional.empty());

        // when
        Movie createdMovie = movieCreatorService.createMovieFromPolishTitle(MATRIX_TITLE, StringFunctions.toURL("https://www.filmweb.pl/film/Matrix-1999-628"));
        createdMovie.setImDbRating(BigDecimal.valueOf(8.7));
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
//    @Test
    void shouldCallCreateMovieFromPolishTitleWhenCreatingFromUrl() {
        // given
        ArgumentCaptor<Movie> movieParam = ArgumentCaptor.forClass(Movie.class);
        when(movieRepository.save(movieParam.capture())).thenAnswer((invocation) -> movieParam.getValue());
        given(actorCreatorService.createActorFromImdbId(any())).willReturn(Optional.empty());
        MovieCreatorService creatorServiceSpy = Mockito.spy(new MovieCreatorService(imdbService, webOperations, movieRepository, actorCreatorService, filmwebMovieCreator));

        // when
        creatorServiceSpy.createProductFromUrl(new URL("https://www.filmweb.pl/film/Matrix-1999-628"));

        // then
        verify(creatorServiceSpy).createMovieFromPolishTitle("Matrix (1999)", new URL("https://www.filmweb.pl/film/Matrix-1999-628"));
    }

}