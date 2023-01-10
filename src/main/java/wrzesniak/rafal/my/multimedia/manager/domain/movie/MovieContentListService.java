package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.MovieNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class MovieContentListService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public MovieContentList addMovieToUserContentList(User user, String listName, @Valid @ImdbId String movieImbdId) {
        Movie movie = movieRepository.findByImdbId(movieImbdId).orElseThrow(MovieNotFoundException::new);
        return addMovieToUserContentList(user, listName, movie);
    }

    public MovieContentList addMovieToUserContentList(User user, String listName, Movie movie) {
        MovieContentList movieContentList = user.getMovieContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        movieContentList.addMovie(movie);
        userRepository.save(user);
        log.info("Added movie: `{}` to list: `{}` for user: {}", movie.getTitle(), listName, user.getUsername());
        return movieContentList;
    }

    public MovieContentList addNewMovieContentListToUser(User user, String listName) {
        MovieContentList contentList = user.addNewMovieList(listName);
        userRepository.save(user);
        return contentList;
    }

    public void removeMovieFromList(User user, String listName, @Valid @ImdbId String movieImdbIdToRemove) {
        Movie movieToRemove = movieRepository.findByImdbId(movieImdbIdToRemove).orElseThrow(MovieNotFoundException::new);
        removeMovieFromList(user, listName, movieToRemove);
    }

    public void removeMovieFromList(User user, String listName, Movie movieToRemove) {
        MovieContentList movieContentList = user.getMovieContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        movieContentList.removeMovie(movieToRemove);
        userRepository.save(user);
        log.info("Movie `{}` removed from list `{}` for user: {}", movieToRemove.getTitle(), listName, user.getUsername());
    }

    public void removeMovieContentList(User user, String listName) {
        MovieContentList list = user.getMovieContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        user.getMovieLists().remove(list);
        userRepository.save(user);
        log.info("Removed list `{}` from user: {}", list, user.getUsername());
    }

    public void addToWatchMovieList(User user, String listName) {
        user.getMovieLists().forEach(movieContentList -> movieContentList.setToWatchList(false));
        user.addNewMovieList(listName).withToWatchList(true);
        userRepository.save(user);
        log.info("Created new to watch movie list with name `{}`", listName);
    }


}
