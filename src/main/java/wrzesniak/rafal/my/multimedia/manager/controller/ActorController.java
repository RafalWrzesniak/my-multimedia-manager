package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ActorContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.ActorList;

@Slf4j
@Validated
@RestController
@RequestMapping("actor")
@RequiredArgsConstructor
public class ActorController {

    private final ActorRepository actorRepository;
    private final UserController userController;
    private final UserService userService;

    @GetMapping("/findByImdbId/{imdbId}")
    public Optional<Actor> getByImdbId(@PathVariable @Valid @ImdbId String imdbId) {
        return actorRepository.findByImdbId(imdbId);
    }

    @GetMapping("/findById/{id}")
    public Optional<Actor> getById(@PathVariable long id) {
        return actorRepository.findById(id);
    }

    @GetMapping("/")
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    @GetMapping("/list/{listName}")
    public ActorContentList getActorContentListByName(@PathVariable String listName) {
        return (ActorContentList) userController.getCurrentUser().getContentListByName(listName, ActorList).orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/list/{listName}")
    public BaseContentList<Actor> addActorContentListToUser(@PathVariable String listName) {
        return userService.addNewContentListToUser(userController.getCurrentUser(), listName, ActorList);
    }

    @DeleteMapping("/list/{listName}")
    public void removeActorList(@PathVariable String listName) {
        userService.removeContentListFromUser(userController.getCurrentUser(), listName, ActorList);
    }

    @PostMapping("/list/{listName}/{actorImdbId}")
    public void addActorToUserContentList(@PathVariable String listName, @PathVariable @Valid @ImdbId String actorImdbId) {
        Actor actor = actorRepository.findByImdbId(actorImdbId).orElseThrow();
        userService.addObjectToContentList(userController.getCurrentUser(), listName, ActorList, actor);
    }

    @DeleteMapping("/list/{listName}/{actorImdbIdToRemove}")
    public void removeActorFromList(@PathVariable String listName, @PathVariable @Valid @ImdbId String actorImdbIdToRemove) {
        Actor actor = actorRepository.findByImdbId(actorImdbIdToRemove).orElseThrow();
        userService.removeObjectFromContentList(userController.getCurrentUser(), listName, ActorList, actor);
    }
}
