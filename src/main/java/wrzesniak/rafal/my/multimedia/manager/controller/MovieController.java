package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.util.Validators;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebService;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieCreatorService movieCreatorService;
    private final MovieRepository movieRepository;
    private final Validators validators;
    private final FilmwebService filmwebService;

    @PostMapping("/findAndCreateMovieByPolishTitle/{title}")
    public Movie findAndCreateMovieByTitle(@PathVariable String title) {
        Optional<Movie> movie = movieCreatorService.createMovieFromPolishTitle(title);
        return movie.orElseThrow();
    }

    @SneakyThrows
    @PostMapping("/findAndCreateMovieByFilmwebUrl/{filmwebUrl}")
    public Movie findAndCreateMovieByFilmwebUrl(@PathVariable String filmwebUrl) {
        URL url = filmwebService.createFilmwebUrlFromPart("/film/" + filmwebUrl);
        return movieCreatorService.createMovieFromFilmwebUrl(url).orElseThrow();
    }

    @GetMapping("/")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Movie> getMovieById(@PathVariable long id) {
        return movieRepository.findById(id);
    }

    @GetMapping("/{imdbId}")
    public Optional<Movie> getMovieByImdbId(@PathVariable String imdbId) {
        if(!validators.isValidImdbId(imdbId)) {
            throw new IllegalArgumentException(String.format("`%s` is not valid imdb id!", imdbId));
        }
        return movieRepository.findByImdbId(imdbId);
    }

}
