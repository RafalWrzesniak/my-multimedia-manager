package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorContentListService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieContentListService;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("movieList")
@RequiredArgsConstructor
public class MovieListController {

    private final ActorContentListService userService;
    private final UserController userController;
    private final MovieContentListService movieContentListService;

    @GetMapping("/{listName}")
    public MovieContentList getMovieContentListByName(@PathVariable String listName) {
        return userController.getCurrentUser().getMovieContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/{listName}")
    public MovieContentList addMovieContentListToUser(@PathVariable String listName) {
        User user = userController.getCurrentUser();
        return movieContentListService.addNewMovieContentListToUser(user, listName);
    }

    @DeleteMapping("/{listName}")
    public void removeMovieList(@PathVariable String listName) {
        movieContentListService.removeMovieContentList(userController.getCurrentUser(), listName);
    }

    @PostMapping("/{listName}/{movieImbdId}")
    public MovieContentList addMovieToUserContentList(@PathVariable String listName, @PathVariable @Valid @ImdbId String movieImbdId) {
        return movieContentListService.addMovieToUserContentList(userController.getCurrentUser(), listName, movieImbdId);
    }

    @DeleteMapping("/{listName}/{movieImdbIdToRemove}")
    public void removeMovieFromList(@PathVariable String listName, @PathVariable @Valid @ImdbId String movieImdbIdToRemove) {
        movieContentListService.removeMovieFromList(userController.getCurrentUser(), listName, movieImdbIdToRemove);
    }

}
