package wrzesniak.rafal.my.multimedia.manager.domain.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.DefaultProductService;
import wrzesniak.rafal.my.multimedia.manager.domain.GenericUserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.repository.GameRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.service.GameCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.net.URL;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GAME_LIST;

@Slf4j
@Service
public class GameFacade extends DefaultProductService<GameWithUserDetailsDto, Game, GameUserDetails, GameListWithUserDetails> {

    private final GameCreatorService gameCreatorService;

    private GameFacade(UserService userService, GameRepository gameRepository, GenericUserObjectDetailsFounder<GameWithUserDetailsDto, Game, GameUserDetails, GameListWithUserDetails> gameUserObjectDetailsFounder, GameCreatorService gameCreatorService) {
        super(userService, gameRepository, gameUserObjectDetailsFounder, GAME_LIST, GameWithUserDetailsDto::getFinishedOn, gameCreatorService, GameUserDetails::withFinishedOn);
        this.gameCreatorService = gameCreatorService;
    }

    public Game createGameFromUrl(URL gryOnlineUrl, GamePlatform gamePlatform) {
        Game game = gameCreatorService.createGameFromUrl(gryOnlineUrl, gamePlatform);
        Optional.ofNullable(gamePlatform).ifPresent(platform -> setPlatformForUserGame(game, platform));
        return game;
    }

    public void setPlatformForUserGame(long gameId, GamePlatform gamePlatform) {
        super.findRawProductById(gameId).ifPresent(game -> setPlatformForUserGame(game, gamePlatform));
    }

    public void setPlatformForUserGame(Game game, GamePlatform gamePlatform) {
        GameUserDetails gameDetails = super.getProductUserDetails(game);
        gameDetails.setGamePlatform(gamePlatform);
        log.info("Marking game `{}` as playing on {}", game.getTitle(), gameDetails.getGamePlatform());
        super.saveUserProductDetails(gameDetails);
    }

    public void setHoursPlayedForUser(long gameId, int playedHours) {
        Game game = super.findRawProductById(gameId).orElseThrow();
        GameUserDetails gameDetails = super.getProductUserDetails(game);
        gameDetails.setPlayedHours(playedHours);
        log.info("Marking game `{}` as spent on {} hours", game.getTitle(), gameDetails.getPlayedHours());
        super.saveUserProductDetails(gameDetails);
    }

}
