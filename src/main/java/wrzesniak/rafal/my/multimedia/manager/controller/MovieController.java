package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.MovieNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.RecentlyWatchedService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MovieList;
import static wrzesniak.rafal.my.multimedia.manager.domain.user.RegistrationService.ALL_MOVIES;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@RestController
@RequestMapping("movie")
@RequiredArgsConstructor
public class MovieController {

    private final RecentlyWatchedService recentlyWatchedService;
    private final MovieCreatorService movieCreatorService;
    private final UserObjectDetailsFounder detailsFounder;
    private final MovieRepository movieRepository;
    private final UserController userController;
    private final UserService userService;

    @PostMapping("/create/title/{title}")
    public Movie findAndCreateMovieByTitle(@PathVariable String title, @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromPolishTitle(title, null);
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        userService.addObjectToListIfExists(userController.getCurrentUser(), ALL_MOVIES, MovieList, movie);
        userService.addObjectToListIfExists(userController.getCurrentUser(), listName, MovieList, movie);
        return movie;
    }

    @PostMapping("/create/filmweb/{filmwebUrl}")
    public Movie findAndCreateMovieByFilmwebUrl(String filmwebUrl, @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromFilmwebUrl(toURL(filmwebUrl));
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        userService.addObjectToListIfExists(userController.getCurrentUser(), ALL_MOVIES, MovieList, movie);
        userService.addObjectToListIfExists(userController.getCurrentUser(), listName, MovieList, movie);
        return movie;
    }

    @PostMapping("/create/imdb/{imdbId}")
    public Movie findAndCreateMovieByImdbId(@PathVariable @Valid @ImdbId String imdbId,
                                            @RequestParam(required = false) URL filmwebUrl,
                                            @RequestParam(required = false) String listName) {
        Optional<Movie> potentialMovie = movieCreatorService.createMovieFromImdbId(imdbId, filmwebUrl);
        Movie movie = potentialMovie.orElseThrow(MovieNotFoundException::new);
        userService.addObjectToListIfExists(userController.getCurrentUser(), ALL_MOVIES, MovieList, movie);
        userService.addObjectToListIfExists(userController.getCurrentUser(), listName, MovieList, movie);
        return movie;
    }

    @PostMapping("/create/bulk/imdb")
    public void findAndCreateBulkMoviesByImdb(@RequestBody List<Pair<String, String>> imdbFilmwebUrl, @RequestParam(required = false) String listName) {
        imdbFilmwebUrl.forEach(pair -> findAndCreateMovieByImdbId(pair.getFirst(), toURL(pair.getSecond()), listName));
    }

    @GetMapping("/markAsWatched/{movieId}")
    public void markMovieAsRecentlyWatched(@PathVariable long movieId,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
        recentlyWatchedService.markMovieAsRecentlyWatched(userController.getCurrentUser(), movie, date);
    }

    @GetMapping("/")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Optional<MovieWithUserDetailsDto> getMovieById(@PathVariable long id) {
        return movieRepository.findById(id)
                .map(movie -> detailsFounder.findDetailedMovieDataFor(movie, userController.getCurrentUser()));
    }

    @GetMapping("/findByImdb/{imdbId}")
    public Optional<MovieWithUserDetailsDto> getMovieByImdbId(@PathVariable @Valid @ImdbId String imdbId) {
        return movieRepository.findByImdbId(imdbId)
                .map(movie -> detailsFounder.findDetailedMovieDataFor(movie, userController.getCurrentUser()));
    }

    @GetMapping("/list/{listName}")
    public MovieListWithUserDetails getMovieContentListByName(@PathVariable String listName) {
        return userController.getCurrentUser().getContentListByName(listName, MovieList)
                .map(baseList -> detailsFounder.findDetailedMovieDataFor((MovieContentList) baseList, userController.getCurrentUser()))
                .orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/list/{listName}")
    public MovieListWithUserDetails addMovieContentListToUser(@PathVariable String listName) {
        User user = userController.getCurrentUser();
        MovieContentList movieContentList = userService.addNewContentListToUser(user, listName, MovieList);
        return detailsFounder.findDetailedMovieDataFor(movieContentList, user);
    }

    @DeleteMapping("/list/{listName}")
    public void removeMovieList(@PathVariable String listName) {
        userService.removeContentListFromUser(userController.getCurrentUser(), listName, MovieList);
    }

    @PostMapping("/list/{listName}/{movieId}")
    public void addMovieToUserContentList(@PathVariable String listName, @PathVariable long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
        userService.addObjectToContentList(userController.getCurrentUser(), listName, MovieList, movie);
    }

    @DeleteMapping("/list/{listName}/remove")
    public void removeMovieFromList(@PathVariable String listName, @RequestBody List<Long> movieIds) {
        for (Long movieId : movieIds) {
            Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
            userService.removeObjectFromContentList(userController.getCurrentUser(), listName, MovieList, movie);
        }
    }

    @PostMapping("/move/movie")
    public void moveMovieFromOneListToAnother(long movieId, String originalList, String targetList, boolean removeFromOriginal) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
        userService.moveObjectFromListToList(userController.getCurrentUser(), movie, MovieList, originalList, targetList, removeFromOriginal);
    }

    @DeleteMapping("/movie/delete")
    public void removeMovieFromDatabase(long movieId) {
        movieRepository.deleteById(movieId);
        log.info("Movie with id {} deleted from database", movieId);
    }

}
