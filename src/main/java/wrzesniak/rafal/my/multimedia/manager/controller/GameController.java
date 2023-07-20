package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;

import java.time.LocalDate;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GAME_LIST;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("game")
public class GameController extends BaseProductController<GameWithUserDetailsDto, Game, GameUserDetails, GameListWithUserDetails> {

    private final GameFacade gameFacade;

    public GameController(GameFacade gameFacade) {
        super(gameFacade);
        this.gameFacade = gameFacade;
    }

    @PostMapping("/createGame")
    public Game createProductFromUrl(@RequestParam String url,
                                     @RequestParam GamePlatform gamePlatform,
                                     @RequestParam(required = false) String listName) {
        Game game = gameFacade.createGameFromUrl(toURL(url), gamePlatform);
        gameFacade.addProductToList(game, GAME_LIST.getAllProductsListName());
        gameFacade.addProductToList(game, listName);
        return game;
    }

    @PostMapping("/{id}/finishGame")
    public void markGameAsFinished(long gameId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                   @RequestParam(required = false) int playedHours) {
        super.markProductAsFinished(gameId, date);
        gameFacade.setHoursPlayedForUser(gameId, playedHours);
    }

    @Override
    @ApiIgnore
    public Game createProductFromUrl(@RequestParam String url,
                                     @RequestParam(required = false) String listName) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /game/createGame");
    }

    @Override
    @ApiIgnore
    public void markProductAsFinished(@PathVariable long id,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishDate) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /game/{id}/finishGame");
    }

}
