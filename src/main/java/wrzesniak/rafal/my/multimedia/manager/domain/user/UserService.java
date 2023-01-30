package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public <LIST extends BaseContentList<?>> LIST addNewContentListToUser(User user, String listName, ContentListType listType) {
        LIST contentList = user.addNewContentList(listName, listType);
        userRepository.save(user);
        log.info("Added new list `{}` for user: {}", listName, user.getUsername());
        return contentList;
    }

    public void removeContentListFromUser(User user, String listName, ContentListType listType) {
        user.removeContentList(listName, listType);
        userRepository.save(user);
        log.info("Deleted list `{}` from user: {}", listName, user.getUsername());
    }

    public <CONTENT, LIST extends BaseContentList<CONTENT>> void addObjectToContentList(User user, String listName, ContentListType listType, CONTENT objectToAdd) {
        BaseContentList<CONTENT> list = (LIST) user.getContentListByName(listName, listType).orElseThrow(NoListWithSuchNameException::new);
        list.addContent(objectToAdd);
        userRepository.save(user);
        log.info("Added to list `{}` object: {}", list.getName(), objectToAdd);
    }

    public <CONTENT, LIST extends BaseContentList<CONTENT>> void removeObjectFromContentList(User user, String listName, ContentListType listType, CONTENT objectToRemove) {
        BaseContentList<CONTENT> list = (LIST) user.getContentListByName(listName, listType).orElseThrow(NoListWithSuchNameException::new);
        list.removeContent(objectToRemove);
        userRepository.save(user);
        log.info("Removed from list `{}` object: {}", list.getName(), objectToRemove);
    }

}
