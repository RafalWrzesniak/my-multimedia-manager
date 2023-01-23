package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.MovieNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoSuchUserException;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.RecentlyWatchedService;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MovieList;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@RestController
@RequestMapping("movie")
@RequiredArgsConstructor
public class MovieController {

    private final RecentlyWatchedService recentlyWatchedService;
    private final MovieCreatorService movieCreatorService;
    private final MovieRepository movieRepository;
    private final UserController userController;
    private final UserService userService;

    @PostMapping("/create/title/{title}")
    public Movie findAndCreateMovieByTitle(@PathVariable String title, @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromPolishTitle(title);
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        addMovieToListIfExist(movie, listName);
        return movie;
    }

    @PostMapping("/create/filmweb/{filmwebUrl}")
    public Movie findAndCreateMovieByFilmwebUrl(String filmwebUrl, @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromFilmwebUrl(toURL(filmwebUrl));
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        addMovieToListIfExist(movie, listName);
        return movie;
    }

    @PostMapping("/create/imdb/{imdbId}")
    public Movie findAndCreateMovieByImdbId(@PathVariable @Valid @ImdbId String imdbId, @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromImdbId(imdbId);
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        addMovieToListIfExist(movie, listName);
        return movie;
    }

    @GetMapping("/markAsWatched/")
    public void markMovieAsRecentlyWatched(@RequestParam long movieId,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
        recentlyWatchedService.markMovieAsRecentlyWatched(movie, date);
    }

    @GetMapping("/")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Optional<Movie> getMovieById(@PathVariable long id) {
        return movieRepository.findById(id);
    }

    @GetMapping("/findByImdb/{imdbId}")
    public Optional<Movie> getMovieByImdbId(@PathVariable @Valid @ImdbId String imdbId) {
        return movieRepository.findByImdbId(imdbId);
    }

    @GetMapping("/list/{listName}")
    public MovieContentList getMovieContentListByName(@PathVariable String listName) {
        return (MovieContentList) userController.getCurrentUser().getContentListByName(listName, MovieList).orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/list/{listName}")
    public MovieContentList addMovieContentListToUser(@PathVariable String listName) {
        User user = userController.getCurrentUser();
        return userService.addNewContentListToUser(user, listName, MovieList);
    }

    @DeleteMapping("/list/{listName}")
    public void removeMovieList(@PathVariable String listName) {
        userService.removeContentListFromUser(userController.getCurrentUser(), listName, MovieList);
    }

    @PostMapping("/list/{listName}/")
    public void addMovieToUserContentList(@PathVariable String listName, @RequestParam long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
        userService.addObjectToContentList(userController.getCurrentUser(), listName, MovieList, movie);
    }

    @DeleteMapping("/list/{listName}/")
    public void removeMovieFromList(@PathVariable String listName, @RequestParam long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
        userService.removeObjectFromContentList(userController.getCurrentUser(), listName, MovieList, movie);
    }

    private void addMovieToListIfExist(Movie movie, String listName) {
        try {
            userService.addObjectToContentList(userController.getCurrentUser(), listName, MovieList, movie);
        } catch (NoListWithSuchNameException e) {
            log.warn("Could not add movie `{}` to list `{}`, because list does not exist!", movie.getTitle(), listName);
        }
        catch(NoSuchUserException noSuchUserException) {
            log.warn("Could not add movie `{}` to list `{}`, because user is unknown!", movie.getTitle(), listName);
        }
    }
}
