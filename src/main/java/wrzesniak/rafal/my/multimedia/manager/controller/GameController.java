package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder;

import java.time.LocalDate;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder.TOKEN_HEADER;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("game")
public class GameController extends BaseProductController<GameWithUserDetailsDto, GameUserDetailsDynamo, GameListWithUserDetails, GameDynamo> {

    private final GameFacade gameFacade;
    private final JwtTokenDecoder jwtTokenDecoder;

    public GameController(GameFacade gameFacade, JwtTokenDecoder jwtTokenDecoder) {
        super(gameFacade, jwtTokenDecoder);
        this.gameFacade = gameFacade;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("/createGameUrl")
    public GameWithUserDetailsDto createProductFromUrl(@RequestParam String url,
                                                       @RequestParam(required = false) GamePlatform gamePlatform,
                                                       @RequestParam(required = false) String listId,
                                                       @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return gameFacade.createGameFromUrl(toURL(url), gamePlatform, username, listId);
    }

    @PostMapping("/finishGame")
    public void markGameAsFinished(@RequestParam String gameId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishDate,
                                   @RequestParam(required = false) Integer playedHours,
                                   @RequestHeader(TOKEN_HEADER) String jwtToken) {
        super.markProductAsFinished(gameId, finishDate, jwtToken);
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        Optional.ofNullable(playedHours).ifPresent(timeSpent -> gameFacade.setHoursPlayedForUser(gameId, timeSpent, username));
    }

    @PostMapping("/platform")
    public void setGamePlatform(@RequestParam String gameId,
                                @RequestParam GamePlatform gamePlatform,
                                @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        gameFacade.setPlatformForUserGame(gameId, gamePlatform, username);
    }

    @Override
    public GameWithUserDetailsDto createProductFromUrl(@RequestParam String url,
                                                       @RequestParam(required = false) String listId,
                                                       @RequestHeader(TOKEN_HEADER) String jwtToken) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /game/createGame");
    }

    @Override
    public void markProductAsFinished(@RequestParam String id,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishDate,
                                      @RequestHeader(TOKEN_HEADER) String jwtToken) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /game/{id}/finishGame");
    }

}
