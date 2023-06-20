package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.DefaultProductService;
import wrzesniak.rafal.my.multimedia.manager.domain.GenericUserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.repository.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.service.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.service.RecentlyWatchedService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.net.URL;
import java.time.LocalDate;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MOVIE_LIST;

@Slf4j
@Service
public class MovieFacade extends DefaultProductService<MovieWithUserDetailsDto, Movie, MovieUserDetails, MovieListWithUserDetails> {

    private final RecentlyWatchedService recentlyWatchedService;
    private final MovieCreatorService movieCreatorService;

    public MovieFacade(UserService userService, MovieRepository movieRepository, GenericUserObjectDetailsFounder<MovieWithUserDetailsDto, Movie, MovieUserDetails, MovieListWithUserDetails> genericUserObjectDetailsFounder, MovieCreatorService movieCreatorService, RecentlyWatchedService recentlyWatchedService) {
        super(userService, movieRepository, genericUserObjectDetailsFounder, MOVIE_LIST, MovieWithUserDetailsDto::getWatchedOn, movieCreatorService, MovieUserDetails::withWatchedOn);
        this.recentlyWatchedService = recentlyWatchedService;
        this.movieCreatorService = movieCreatorService;
    }

    @Override
    public void markProductAsFinished(long productId, LocalDate finishDate) {
        super.findRawProductById(productId)
                .ifPresent(movie -> recentlyWatchedService.markMovieAsRecentlyWatched(movie, finishDate));
    }

    public Movie createMovieFromPolishTitle(String polishTitle, URL filmwebUrl) {
        return movieCreatorService.createMovieFromPolishTitle(polishTitle, filmwebUrl);
    }

    public Movie createMovieFromImdbId(String imdbId, URL filmwebUrl) {
        return movieCreatorService.createMovieFromImdbId(imdbId, filmwebUrl);
    }
}