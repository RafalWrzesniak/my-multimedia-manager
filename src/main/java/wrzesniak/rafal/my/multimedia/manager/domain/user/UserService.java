package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ContentListDynamoService contentListDynamoService;
    private final DynamoDbClientGeneric<UserDynamo> userDynamoDb;

    private void createAllContentListForNewUser(String username) {
        Arrays.stream(ContentListType.values())
                .forEach(contentListType -> contentListDynamoService.createContentList(contentListType.getAllProductsListName(),
                        username, contentListType, true));
        log.info("New users list created successfully: {}", username);
    }

    public UserDynamo createNewUser(String username, String preferredUsername, String email) {
        UserDynamo user = new UserDynamo(username, preferredUsername, email);
        userDynamoDb.saveItem(user);
        createAllContentListForNewUser(username);
        log.info("New user created successfully: {}, {}", username, preferredUsername);
        return user;
    }
}
