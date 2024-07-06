package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoSuchUserException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ContentListDynamoService contentListDynamoService;
    private final DynamoDbClientGeneric<UserDynamo> userDynamoDb;

    public List<ContentListDynamo> createAllContentListForNewUser(String username) {
        List<ContentListDynamo> lists = Arrays.stream(ContentListType.values())
                .map(contentListType -> contentListDynamoService.createContentList(contentListType.getAllProductsListName(),
                        username, contentListType, true))
                .toList();
        log.info("New users list created successfully: {}", username);
        return lists;
    }

    public void setSynchronizationInfo(String username, SyncInfo syncInfo) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        userDynamo.setLastSynchronization(syncInfo.syncTimestamp());
        userDynamoDb.saveItem(userDynamo);
    }

    public SyncInfo getLastSynchronizationInfo(String username) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        return new SyncInfo(userDynamo.getLastSynchronization());
    }

    public UserDynamo createNewUser(String username, String preferredUsername, String email) {
        UserDynamo user = new UserDynamo(username, preferredUsername, email);
        userDynamoDb.saveItem(user);
        createAllContentListForNewUser(username);
        log.info("New user created successfully: {}, {}", username, preferredUsername);
        return user;
    }

    public void markUserLoggedIn(String username) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        userDynamo.markedLoggedIn();
        userDynamoDb.saveItem(userDynamo);
    }

}
