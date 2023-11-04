package wrzesniak.rafal.my.multimedia.manager.domain.game.objects;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;
import wrzesniak.rafal.my.multimedia.manager.util.GamePlatformSetConverter;
import wrzesniak.rafal.my.multimedia.manager.util.PlayModeSetConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@With
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameDynamo implements Product {

    @EqualsAndHashCode.Include
    private String title;
    @EqualsAndHashCode.Include
    private URL gryOnlineUrl;
    private String description;
    private BigDecimal ratingValue;
    private Integer ratingCount;
    private String studio;
    private String publisher;
    private Set<PlayMode> playModes;
    private Set<GamePlatform> gamePlatform;
    private Set<String> genreList;
    private LocalDate releaseDate;
    private String webImageUrl;
    private LocalDateTime createdOn;

    @DynamoDbPartitionKey
    public URL getGryOnlineUrl() {
        return gryOnlineUrl;
    }

    @Override
    public String getId() {
        return gryOnlineUrl.toString();
    }

    @DynamoDbConvertedBy(PlayModeSetConverter.class)
    public Set<PlayMode> getPlayModes() {
        return playModes;
    }

    @DynamoDbConvertedBy(GamePlatformSetConverter.class)
    public Set<GamePlatform> getGamePlatform() {
        return gamePlatform;
    }
}
