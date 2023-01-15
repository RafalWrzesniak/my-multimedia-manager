package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.controller.UserController;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecentlyWatchedService {

    private static final String RECENTLY_WATCHED = "Ostatnio oglądnięte";

    private final UserController userController;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public void markMovieAsRecentlyWatched(Movie movie) {
        markMovieAsRecentlyWatched(movie, LocalDate.now());
    }

    public void markMovieAsRecentlyWatched(Movie movie, LocalDate watchedDate) {
        log.info("Marking movie `{}` as recently watched", movie.getTitle());
        setMovieWatchedOnDate(movie, watchedDate);
        User user = userController.getCurrentUser();
        addMovieToUserRecentlyWatchedMovies(user, movie);
        removeMovieFromToWatchList(user, movie);
        userRepository.save(user);
    }

    private void setMovieWatchedOnDate(Movie movie, LocalDate watchedDate) {
        movie.setWatchedOn(watchedDate);
        movieRepository.save(movie);
    }

    private void addMovieToUserRecentlyWatchedMovies(User user, Movie movie) {
        MovieContentList list = findUsersRecentlyWatchedList(user);
        list.addMovie(movie);
    }

    private void removeMovieFromToWatchList(User user, Movie movie) {
        Optional<MovieContentList> list = findUsersToWatchList(user);
        list.ifPresent(movieContentList -> movieContentList.removeMovie(movie));
    }

    private MovieContentList findUsersRecentlyWatchedList(User user) {
        return user.getMovieLists().stream()
                .filter(MovieContentList::isRecentlyWatchedList)
                .findFirst()
                .orElse(new MovieContentList(RECENTLY_WATCHED)
                        .withRecentlyWatchedList(true));
    }

    private Optional<MovieContentList> findUsersToWatchList(User user) {
        return user.getMovieLists().stream()
                .filter(MovieContentList::isToWatchList)
                .findFirst();
    }

}
