package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import wrzesniak.rafal.my.multimedia.manager.util.SynchronizationConverter;

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
    private List<SyncInfo> lastSynchronization;

    public UserDynamo(String username, String preferredUsername, String email) {
        this.username = username;
        this.preferredUsername = preferredUsername;
        this.email = email;
        this.createdOn = LocalDateTime.now();
        this.loggedInTimestamps = new ArrayList<>();
        this.lastSynchronization = new ArrayList<>();
    }

    public void addNewSynchronization(SyncInfo syncInfo) {
        if(lastSynchronization == null) lastSynchronization = new ArrayList<>();
        lastSynchronization.addFirst(syncInfo);
        if(lastSynchronization.size() > 15) {
            lastSynchronization.removeLast();
        }
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

    @DynamoDbConvertedBy(SynchronizationConverter.class)
    public List<SyncInfo> getLastSynchronization() {
        return lastSynchronization;
    }
}
