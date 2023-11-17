package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.error.UserAlreadyExistException;

import java.util.Arrays;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.values;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

//    private final PasswordEncoder passwordEncoder;
    private final DynamoDbClientGeneric<UserDynamo> userDynamoClient;
    private final ContentListDynamoService contentListDynamoService;


    public UserDynamo registerNewUserAccount(LoginCredentials credentials) throws UserAlreadyExistException {
        if(userDynamoClient.getItemById(credentials.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }
//        UserDynamo user = new UserDynamo(credentials.getUsername(), passwordEncoder.encode(credentials.getPassword()));
        UserDynamo user = new UserDynamo(credentials.getUsername(), credentials.getPassword());
        addDefaultListsFor(user);
        userDynamoClient.saveItem(user);
        log.info("New user register successfully: {}", user);
        return user;
    }

    private void addDefaultListsFor(UserDynamo user) {
        Arrays.stream(values())
                .forEach(contentListType -> contentListDynamoService.createContentList(contentListType.getAllProductsListName(),
                        user.getUsername(), contentListType, true));
    }

}
