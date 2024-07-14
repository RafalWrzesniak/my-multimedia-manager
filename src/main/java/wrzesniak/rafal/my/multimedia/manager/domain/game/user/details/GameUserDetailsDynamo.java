package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductUserDetailsAbstract;

import java.time.LocalDate;
import java.time.LocalDateTime;

@With
@Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameUserDetailsDynamo extends ProductUserDetailsAbstract<GameUserDetailsDynamo> {

    private String username;
    private String gameId;
    private LocalDate finishedOn;
    private GamePlatform gamePlatform;
    private Integer playedHours;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public GameUserDetailsDynamo(String username, String gameId) {
        this.username = username;
        this.gameId = gameId;
        this.createdOn = LocalDateTime.now();
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @DynamoDbSortKey
    public String getGameId() {
        return gameId;
    }

    @Override
    public String getId() {
        return gameId;
    }
}
