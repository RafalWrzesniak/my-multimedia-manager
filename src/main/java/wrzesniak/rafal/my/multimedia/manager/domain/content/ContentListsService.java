package wrzesniak.rafal.my.multimedia.manager.domain.content;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserRepository;

@Slf4j
@Service
public class ContentListsService<T> {

    private final UserRepository userRepository;

    public ContentListsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addObjectToUserContentList(User user, String listName, T object) {
    }



}
