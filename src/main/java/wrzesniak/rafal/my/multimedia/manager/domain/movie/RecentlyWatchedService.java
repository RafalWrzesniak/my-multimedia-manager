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
    private static final int MAX_RECENTLY_WATCHED_SIZE = 12;

    private final UserController userController;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public void markMovieAsRecentlyWatched(Movie movie, LocalDate date) {
        log.info("Marking movie `{}` as recently watched", movie.getTitle());
        setMovieWatchedOnDate(movie, date);
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
        MovieContentList recentlyWatchedList = findUsersRecentlyWatchedList(user);
        recentlyWatchedList.addContent(movie);
        if(recentlyWatchedList.getAllContent().size() > MAX_RECENTLY_WATCHED_SIZE) {
            recentlyWatchedList.getAllContent().remove(0);
        }
    }

    private void removeMovieFromToWatchList(User user, Movie movie) {
        Optional<MovieContentList> list = findUsersToWatchList(user);
        list.ifPresent(movieContentList -> movieContentList.removeContent(movie));
    }

    private MovieContentList findUsersRecentlyWatchedList(User user) {
        return (MovieContentList) user.getMovieLists().stream()
                .filter(contentList -> ((MovieContentList) contentList).isRecentlyWatchedList())
                .findFirst()
                .orElse(new MovieContentList(RECENTLY_WATCHED)
                        .withRecentlyWatchedList(true));
    }

    private Optional<MovieContentList> findUsersToWatchList(User user) {
        return user.getMovieLists().stream()
                .filter(contentList -> ((MovieContentList) contentList).isToWatchList())
                .findFirst()
                .map(movieBaseContentList -> (MovieContentList) movieBaseContentList);
    }

}
