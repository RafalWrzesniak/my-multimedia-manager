package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoSuchUserException;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);
    }

    public <LIST extends BaseContentList<?>> LIST addNewContentListToUser(User user, String listName, ContentListType listType) {
        LIST contentList = user.addNewContentList(listName, listType);
        userRepository.save(user);
        log.info("Added new list `{}` for user: {}", listName, user.getUsername());
        return (LIST) user.getContentListByName(listName, listType).orElseThrow(NoListWithSuchNameException::new);
    }

    public void removeContentListFromUser(User user, String listName, ContentListType listType) {
        user.removeContentList(listName, listType);
        userRepository.save(user);
        log.info("Deleted list `{}` from user: {}", listName, user.getUsername());
    }

    public <CONTENT, LIST extends BaseContentList<CONTENT>> void addObjectToContentList(User user, String listName, ContentListType listType, CONTENT objectToAdd) {
        BaseContentList<CONTENT> list = (LIST) user.getContentListByName(listName, listType).orElseThrow(NoListWithSuchNameException::new);
        if(list.addContent(objectToAdd)) {
            log.info("Added to list `{}` object: {}", list.getName(), objectToAdd);
        }
         else {
             log.info("List `{}` already have object: {}", list.getName(), objectToAdd);
        }
        userRepository.save(user);
    }

    public <CONTENT, LIST extends BaseContentList<CONTENT>> void removeObjectFromContentList(User user, String listName, ContentListType listType, CONTENT objectToRemove) {
        BaseContentList<CONTENT> list = (LIST) user.getContentListByName(listName, listType).orElseThrow(NoListWithSuchNameException::new);
        list.removeContent(objectToRemove);
        userRepository.save(user);
        log.info("Removed from list `{}` object: {}", list.getName(), objectToRemove);
    }

    public <CONTENT> void addObjectToListIfExists(User user, String listName, ContentListType listType, CONTENT objectToAdd) {
        try {
            addObjectToContentList(user, listName, listType, objectToAdd);
        } catch (NoListWithSuchNameException e) {
            if(listName != null) {
                log.warn("Could not add object `{}` to list `{}`, because list does not exist!", objectToAdd, listName);
            }
        }
        catch(NoSuchUserException noSuchUserException) {
            log.warn("Could not add object `{}` to list `{}`, because user is unknown!", objectToAdd, listName);
        }
    }

    public <T> void moveObjectFromListToList(User user, T object, ContentListType listType, String originalList, String targetList, boolean removeFromOriginal) {
        addObjectToContentList(user, targetList, listType, object);
        if(removeFromOriginal) {
            removeObjectFromContentList(user, originalList, listType, object);
        }
    }

}
