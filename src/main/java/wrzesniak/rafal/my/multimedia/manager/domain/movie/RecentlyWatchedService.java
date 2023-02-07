package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MovieList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecentlyWatchedService {

    private static final String RECENTLY_WATCHED = "Ostatnio oglądnięte";
    private static final int MAX_RECENTLY_WATCHED_SIZE = 12;

    private final UserService userService;
    private final MovieUserDetailsRepository movieUserDetailsRepository;

    public void markMovieAsRecentlyWatched(User user, Movie movie, LocalDate date) {
        log.info("Marking movie `{}` as recently watched", movie.getTitle());
        setMovieWatchedOnDate(movie, user, date);
        addMovieToUserRecentlyWatchedMovies(user, movie);
        removeMovieFromToWatchList(user, movie);
    }

    private void setMovieWatchedOnDate(Movie movie, User user, LocalDate watchedDate) {
        MovieUserId movieUserId = MovieUserId.of(movie, user);
        Optional<MovieUserDetails> repoDetails = movieUserDetailsRepository.findById(movieUserId);
        MovieUserDetails movieDetails = repoDetails.orElse(new MovieUserDetails(movieUserId));
        movieDetails.setWatchedOn(firstNonNull(watchedDate, LocalDate.now()));
        movieUserDetailsRepository.save(movieDetails);
    }

    private void addMovieToUserRecentlyWatchedMovies(User user, Movie movie) {
        MovieContentList recentlyWatchedList = findUsersRecentlyWatchedList(user);
        userService.addObjectToContentList(user, recentlyWatchedList.getName(), MovieList, movie);
        List<Movie> moviesToRemove = recentlyWatchedList.getContentList().stream()
                .map(movie1 -> Pair.of(movie1, movieUserDetailsRepository.findById(MovieUserId.of(movie1, user))))
                .sorted(Comparator.comparing(pair -> pair.getSecond().orElseThrow().getWatchedOn()))
                .skip(MAX_RECENTLY_WATCHED_SIZE)
                .map(Pair::getFirst)
                .toList();
        moviesToRemove.forEach(movieToRemove -> userService.removeObjectFromContentList(user, recentlyWatchedList.getName(), MovieList, movieToRemove));
    }

    private void removeMovieFromToWatchList(User user, Movie movie) {
        Optional<MovieContentList> list = findUsersToWatchList(user);
        list.ifPresent(movieContentList -> userService.removeObjectFromContentList(user, movieContentList.getName(), MovieList, movie));
    }

    private MovieContentList findUsersRecentlyWatchedList(User user) {
        return user.getMovieLists().stream()
                .filter(MovieContentList::isRecentlyWatchedList)
                .findFirst()
                .orElseGet(() -> {
                    MovieContentList recentlyWatched = userService.addNewContentListToUser(user, RECENTLY_WATCHED, MovieList);
                    recentlyWatched.setRecentlyWatchedList(true);
                    return recentlyWatched;
                });
    }

    private Optional<MovieContentList> findUsersToWatchList(User user) {
        return user.getMovieLists().stream()
                .filter(MovieContentList::isToWatchList)
                .findFirst();
    }

}
