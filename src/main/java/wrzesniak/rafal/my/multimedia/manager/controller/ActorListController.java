package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorContentListService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ActorContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("actorList")
@RequiredArgsConstructor
public class ActorListController {

    private final ActorContentListService actorContentListService;
    private final UserController userController;
    private final UserRepository userRepository;

    @GetMapping("/{listName}")
    public ActorContentList getActorContentListByName(@PathVariable String listName) {
        return userController.getCurrentUser().getActorContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/{listName}")
    public ActorContentList addActorContentListToUser(@PathVariable String listName) {
        User user = userController.getCurrentUser();
        return actorContentListService.addActorContentListToUser(user, listName);
    }

    @DeleteMapping("/{listName}")
    public void removeActorList(@PathVariable String listName) {
        actorContentListService.removeActorContentList(userController.getCurrentUser(), listName);
    }

    @PostMapping("/{listName}/{actorImdbId}")
    public ActorContentList addActorToUserContentList(@PathVariable String listName, @PathVariable @Valid @ImdbId String actorImdbId) {
        return actorContentListService.addActorToUserContentList(userController.getCurrentUser(), listName, actorImdbId);
    }

    @DeleteMapping("/{listName}/{actorImdbIdToRemove}")
    public void removeMovieFromList(@PathVariable String listName, @PathVariable @Valid @ImdbId String actorImdbIdToRemove) {
        actorContentListService.removeActorFromList(userController.getCurrentUser(), listName, actorImdbIdToRemove);
    }

}
