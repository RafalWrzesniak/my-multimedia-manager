package wrzesniak.rafal.my.multimedia.manager.domain.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.service.GameCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsDtoDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.product.DefaultProductService;

import java.net.URL;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GAME_LIST;

@Slf4j
@Service
public class GameFacade extends DefaultProductService<GameWithUserDetailsDto, GameUserDetailsDtoDynamo, GameListWithUserDetails, GameDynamo> {

    private final GameCreatorService gameCreatorService;

    private GameFacade(DefaultDynamoRepository<GameWithUserDetailsDto, GameUserDetailsDtoDynamo, GameDynamo> gameDynamoRepository,
                       GameCreatorService gameCreatorService,
                       ContentListDynamoService contentListDynamoService) {

        super(GAME_LIST, GameListWithUserDetails::of, contentListDynamoService, gameCreatorService, gameDynamoRepository);
        this.gameCreatorService = gameCreatorService;
    }

    public GameWithUserDetailsDto createGameFromUrl(URL gryOnlineUrl, GamePlatform gamePlatform, String username) {
        GameWithUserDetailsDto game = gameCreatorService.createGameFromUrl(gryOnlineUrl, gamePlatform, username);
        Optional.ofNullable(gamePlatform).ifPresent(platform -> setPlatformForUserGame(game.getGryOnlineUrl().toString(), platform, username));
        return game;
    }


    public void setPlatformForUserGame(String gameId, GamePlatform gamePlatform, String username) {
        GameUserDetailsDtoDynamo gameDetails = super.getProductUserDetails(gameId, username);
        gameDetails.setGamePlatform(gamePlatform);
        log.info("Marking game `{}` as playing on {}", gameId, gameDetails.getGamePlatform());
        super.updateUserProductDetails(gameDetails);
    }

    public void setHoursPlayedForUser(String gameId, int playedHours, String username) {
        GameUserDetailsDtoDynamo gameDetails = super.getProductUserDetails(gameId, username);
        gameDetails.setPlayedHours(playedHours);
        log.info("Marking game `{}` as spent on {} hours", gameId, gameDetails.getPlayedHours());
        super.updateUserProductDetails(gameDetails);
    }

}
