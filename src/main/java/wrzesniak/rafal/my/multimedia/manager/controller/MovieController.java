package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.error.MovieNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;
import wrzesniak.rafal.my.multimedia.manager.util.Validators;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebConfiguration;
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
    private final FilmwebService filmwebService;
    private final FilmwebConfiguration filmwebConfiguration;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Validators validators;

    @PostMapping("/create/title/{title}")
    public Movie findAndCreateMovieByTitle(@PathVariable String title, @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromPolishTitle(title);
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        addMovieToListIfExist(movie, listName);
        return movie;
    }

    @PostMapping("/create/filmweb/{filmwebUrl}")
    public Movie findAndCreateMovieByFilmwebUrl(@PathVariable String filmwebUrl, @RequestParam(required = false) String listName) {
        String urlPrefix = filmwebConfiguration.getLink().getPrefix().get(Movie.class.getSimpleName().toLowerCase());
        URL url = filmwebService.createFilmwebUrlFromPart(urlPrefix + filmwebUrl);
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromFilmwebUrl(url);
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        addMovieToListIfExist(movie, listName);
        return movie;
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
        validators.validateImdbId(imdbId);
        return movieRepository.findByImdbId(imdbId);
    }

    private User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(username).orElseThrow();
    }

    private void addMovieToListIfExist( Movie movie, String listName) {
        try {
            userService.addMovieToUserContentList(getCurrentUser(), listName, movie);
        } catch (NoListWithSuchNameException e) {
            if(listName != null) {
                log.warn("Could not add movie `{}` to list `{}`, because it does not exist!", movie.getTitle(), listName);
            }
        }
    }
}
