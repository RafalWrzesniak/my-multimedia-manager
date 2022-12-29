package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ActorContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.MovieNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.util.Validators;

import java.util.function.Function;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final Validators validators;
    private final ActorRepository actorRepository;

    public MovieContentList addMovieToUserContentList(User user, String listName, String movieImbdId) {
        validators.validateImdbId(movieImbdId);
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

    public ActorContentList addActorToUserContentList(User user, String listName, String actorImdbId) {
        validators.validateImdbId(actorImdbId);
        Actor actor = actorRepository.findByImdbId(actorImdbId).orElseThrow();
        return addActorToUserContentList(user, listName, actor);
    }

    public ActorContentList addActorToUserContentList(User user, String listName, Actor actor) {
        ActorContentList actorContentList = user.getActorContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        actorContentList.addActor(actor);
        userRepository.save(user);
        log.info("Added actor: `{}` to list: `{}` for user: {}", actor.getName(), listName, user.getUsername());
        return actorContentList;
    }

    public ContentList addNewContentListToUser(User user, String listName, Function<String, ContentList> addingFunction) {
        ContentList contentList = addingFunction.apply(listName);
        userRepository.save(user);
        log.info("Added new list `{}` for user: {}", listName, user.getUsername());
        return contentList;
    }

    public MovieContentList addNewMovieContentListToUser(User user, String listName) {
        MovieContentList contentList = user.addNewMovieList(listName);
        userRepository.save(user);
        return contentList;
    }

    public void removeMovieFromList(User user, String listName, Movie movieToRemove) {
        MovieContentList movieContentList = user.getMovieContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        movieContentList.removeMovie(movieToRemove);
        userRepository.save(user);
        log.info("Movie `{}` removed from list `{}` for user: {}", movieToRemove.getTitle(), listName, user.getUsername());
    }

    public void removeActorFromList(User user, String listName, Actor actorToRemove) {
        ActorContentList actorContentList = user.getActorContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        actorContentList.removeActor(actorToRemove);
        userRepository.save(user);
        log.info("Actor `{}` removed from list `{}` for user: {}", actorToRemove.getName(), listName, user.getUsername());
    }

    public void removeMovieContentList(User user, String listName) {
        MovieContentList list = user.getMovieContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        user.getMovieLists().remove(list);
        userRepository.save(user);
        log.info("Removed list `{}` from user: {}", list, user.getUsername());
    }

    public void removeActorContentList(User user, String listName) {
        ActorContentList list = user.getActorContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        user.getActorList().remove(list);
        userRepository.save(user);
        log.info("Removed list `{}` from user: {}", list, user.getUsername());
    }


}
