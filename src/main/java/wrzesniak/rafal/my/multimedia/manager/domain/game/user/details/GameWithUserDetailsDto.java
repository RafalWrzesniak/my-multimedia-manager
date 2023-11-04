package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.PlayMode;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class GameWithUserDetailsDto {

    String id;
    String title;
    URL gryOnlineUrl;
    String description;
    BigDecimal ratingValue;
    Integer ratingCount;
    String studio;
    String publisher;
    Set<PlayMode> playModes;
    Set<GamePlatform> gamePlatform;
    Set<String> genreList;
    LocalDate releaseDate;
    LocalDateTime createdOn;
    LocalDateTime updatedOn;
    GamePlatform userGamePlatform;
    LocalDate finishedOn;
    Integer playedHours;
    String webImageUrl;


    public static GameWithUserDetailsDto of(GameDynamo game, GameUserDetailsDtoDynamo gameUserDetailsDynamo) {
        return GameWithUserDetailsDto.builder()
                .id(game.getId())
                .title(game.getTitle())
                .gryOnlineUrl(game.getGryOnlineUrl())
                .description(game.getDescription())
                .ratingValue(game.getRatingValue())
                .ratingCount(game.getRatingCount())
                .studio(game.getStudio())
                .publisher(game.getPublisher())
                .playModes(game.getPlayModes())
                .gamePlatform(game.getGamePlatform())
                .genreList(game.getGenreList())
                .releaseDate(game.getReleaseDate())
                .createdOn(gameUserDetailsDynamo.getCreatedOn())
                .updatedOn(gameUserDetailsDynamo.getUpdatedOn())
                .userGamePlatform(gameUserDetailsDynamo.getGamePlatform())
                .finishedOn(gameUserDetailsDynamo.getFinishedOn())
                .playedHours(gameUserDetailsDynamo.getPlayedHours())
                .webImageUrl(game.getWebImageUrl())
                .build();
    }

    public static GameWithUserDetailsDto of(Game game, GameUserDetails details) {
        return GameWithUserDetailsDto.builder()
                .id(game.getId().toString())
                .title(game.getTitle())
                .gryOnlineUrl(game.getGryOnlineUrl())
                .description(game.getDescription())
                .ratingValue(game.getRatingValue())
                .ratingCount(game.getRatingCount())
                .studio(game.getStudio())
                .publisher(game.getPublisher())
                .playModes(game.getPlayModes())
                .gamePlatform(game.getGamePlatform())
                .genreList(game.getGenreList())
                .releaseDate(game.getReleaseDate())
                .createdOn(game.getCreatedOn().atStartOfDay())
                .userGamePlatform(details.getGamePlatform())
                .finishedOn(details.getFinishedOn())
                .playedHours(details.getPlayedHours())
                .webImageUrl("/" + game.getImagePath().get(0).subpath(3, 6).toString().replaceAll("\\\\", "/"))
                .build();
    }

}
