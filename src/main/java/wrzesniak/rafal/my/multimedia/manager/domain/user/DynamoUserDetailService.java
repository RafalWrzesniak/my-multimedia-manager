package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;

@Component
@RequiredArgsConstructor
public class DynamoUserDetailService implements UserDetailsService {

    private final DynamoDbClientGeneric<UserDynamo> userDynamoClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDynamoClient.getItemById(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
