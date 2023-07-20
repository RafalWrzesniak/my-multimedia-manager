package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;
import java.net.URL;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("movie")
public class MovieController extends BaseProductController<MovieWithUserDetailsDto, Movie, MovieUserDetails, MovieListWithUserDetails> {

    private final MovieFacade movieFacade;

    public MovieController(MovieFacade movieFacade) {
        super(movieFacade);
        this.movieFacade = movieFacade;
    }

    @PostMapping("/create/title/{polishTitle}")
    public Movie findAndCreateMovieByPolishTitle(@PathVariable String polishTitle,
                                                 @RequestParam(required = false) URL filmwebUrl,
                                                 @RequestParam(required = false) String listName) {
        Movie movie = movieFacade.createMovieFromPolishTitle(polishTitle, filmwebUrl);
        movieFacade.addProductToList(movie, listName);
        return movie;
    }

    @PostMapping("/create/imdb/{imdbId}")
    public Movie findAndCreateMovieByImdbId(@PathVariable @Valid @ImdbId String imdbId,
                                            @RequestParam(required = false) URL filmwebUrl,
                                            @RequestParam(required = false) String listName) {
        Movie movie = movieFacade.createMovieFromImdbId(imdbId, filmwebUrl);
        movieFacade.addProductToList(movie, listName);
        return movie;
    }

}
