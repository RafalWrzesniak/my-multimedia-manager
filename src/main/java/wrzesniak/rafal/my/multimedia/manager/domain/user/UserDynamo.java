package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class UserDynamo {

    private String username;
    private String preferredUsername;
    private String email;
    private LocalDateTime createdOn;
    private List<LocalDateTime> loggedInTimestamps;

    public UserDynamo(String username, String preferredUsername, String email) {
        this.username = username;
        this.preferredUsername = preferredUsername;
        this.email = email;
        this.createdOn = LocalDateTime.now();
        this.loggedInTimestamps = new ArrayList<>();
    }

    public void markedLoggedIn() {
        if(loggedInTimestamps == null) loggedInTimestamps = new ArrayList<>();
        loggedInTimestamps.addFirst(LocalDateTime.now());
        if(loggedInTimestamps.size() > 30) {
            loggedInTimestamps.removeLast();
        }
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

}
