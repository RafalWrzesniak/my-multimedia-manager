package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.PlayMode;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
public class GameWithUserDetailsDto {

    long id;
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
    LocalDate createdOn;
    GamePlatform userGamePlatform;
    LocalDate finishedOn;
    Integer playedHours;
    String imagePath;

    public static GameWithUserDetailsDto of(Game game, GameUserDetails details) {
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
                .createdOn(game.getCreatedOn())
                .userGamePlatform(details.getGamePlatform())
                .finishedOn(details.getFinishedOn())
                .playedHours(details.getPlayedHours())
                .imagePath("/" + game.getImagePath().get(0).subpath(3, 6).toString().replaceAll("\\\\", "/"))
                .build();
    }

}
