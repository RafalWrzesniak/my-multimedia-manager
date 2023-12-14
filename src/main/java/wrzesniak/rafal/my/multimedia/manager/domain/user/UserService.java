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

    public List<ContentListDynamo> createAllContentListForNewUser(String username) {
        List<ContentListDynamo> allContentLists;
        allContentLists = Arrays.stream(ContentListType.values())
                .map(contentListType -> contentListDynamoService.createContentList(contentListType.getAllProductsListName(),
                        username, contentListType, true))
                .toList();
        log.info("New users list created successfully: {}", username);
        return allContentLists;
    }

    public void saveNewUser(String username) {
        UserDynamo user = new UserDynamo(username);
        userDynamoDb.saveItem(user);
    }
}
