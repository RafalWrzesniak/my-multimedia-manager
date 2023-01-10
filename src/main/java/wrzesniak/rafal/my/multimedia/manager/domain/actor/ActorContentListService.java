package wrzesniak.rafal.my.multimedia.manager.domain.actor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ActorContentList;
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
public class ActorContentListService {

    private final UserRepository userRepository;
    private final ActorRepository actorRepository;

    public ActorContentList addActorToUserContentList(User user, String listName, @Valid @ImdbId String actorImdbId) {
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

    public ActorContentList addActorContentListToUser(User user, String listName) {
        ActorContentList contentList = user.addNewActorList(listName);
        userRepository.save(user);
        log.info("Added new list `{}` for user: {}", listName, user.getUsername());
        return contentList;
    }

    public void removeActorFromList(User user, String listName, @Valid @ImdbId String actorImdbIdToRemove) {
        Actor actorToRemove = actorRepository.findByImdbId(actorImdbIdToRemove).orElseThrow();
        removeActorFromList(user, listName, actorToRemove);
    }

    public void removeActorFromList(User user, String listName, Actor actorToRemove) {
        ActorContentList actorContentList = user.getActorContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        actorContentList.removeActor(actorToRemove);
        userRepository.save(user);
        log.info("Actor `{}` removed from list `{}` for user: {}", actorToRemove.getName(), listName, user.getUsername());
    }

    public void removeActorContentList(User user, String listName) {
        ActorContentList list = user.getActorContentListByName(listName).orElseThrow(NoListWithSuchNameException::new);
        user.getActorList().remove(list);
        userRepository.save(user);
        log.info("Removed list `{}` from user: {}", list, user.getUsername());
    }

}
