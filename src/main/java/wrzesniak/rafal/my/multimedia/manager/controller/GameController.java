package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.GameContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.*;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GameList;
import static wrzesniak.rafal.my.multimedia.manager.domain.user.RegistrationService.ALL_GAMES;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class GameController {

    private static final int PAGE_SIZE = 20;
    private final GameService gameService;
    private final UserService userService;
    private final GameRepository gameRepository;
    private final UserController userController;
    private final UserObjectDetailsFounder detailsFounder;

    @PostMapping("/{gameUrl}/{listName}")
    public Game createGameFromUrl(String gameUrl,
                                  @RequestParam(required = false) GamePlatform gamePlatform,
                                  @RequestParam(required = false) String listName) {
        Game game = gameService.createGameFromUrl(toURL(gameUrl), gamePlatform);
        User currentUser = userController.getCurrentUser();
        gameService.setPlatformForUserGame(game, currentUser, gamePlatform);
        userService.addObjectToListIfExists(currentUser, ALL_GAMES, GameList, game);
        userService.addObjectToListIfExists(currentUser, listName, GameList, game);
        return game;
    }

    @GetMapping("/findById/{gameId}")
    public Optional<GameWithUserDetailsDto> getGameById(long gameId) {
        return gameRepository.findById(gameId)
                .map(game -> detailsFounder.findDetailedGameDataFor(game, userController.getCurrentUser()));
    }

    @GetMapping("/findByStudio/{studio}")
    public List<GameWithUserDetailsDto> getGamesByStudio(@RequestParam String studio,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                         @RequestParam(defaultValue = "id") String sortKey,
                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey));
        return gameRepository.findByStudio(studio, pageRequest).stream()
                .map(game -> detailsFounder.findDetailedGameDataFor(game, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/findByPublisher/{studio}")
    public List<GameWithUserDetailsDto> getGamesByPublisher(@RequestParam String publisher,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                         @RequestParam(defaultValue = "id") String sortKey,
                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey));
        return gameRepository.findByPublisher(publisher, pageRequest).stream()
                .map(game -> detailsFounder.findDetailedGameDataFor(game, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/findByPlatform")
    public List<GameWithUserDetailsDto> getGamesByPlatform(@RequestParam GamePlatform gamePlatform,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                            @RequestParam(defaultValue = "id") String sortKey,
                                                            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey));
        return gameRepository.findByGamePlatform(gamePlatform, pageRequest).stream()
                .map(game -> detailsFounder.findDetailedGameDataFor(game, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/findByPlayMode")
    public List<GameWithUserDetailsDto> getGamesByPlayMode(@RequestParam PlayMode playMode,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                           @RequestParam(defaultValue = "id") String sortKey,
                                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey));
        return gameRepository.findByPlayModes(playMode, pageRequest).stream()
                .map(game -> detailsFounder.findDetailedGameDataFor(game, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/")
    public List<Game> getAllGames(@RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                  @RequestParam(defaultValue = "id") String sortKey,
                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey));
        return gameRepository.findAllGames(pageRequest);
    }

    @PostMapping("/markAsFinished/{gameId}")
    public void markGameAsFinished(long gameId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                   @RequestParam(required = false) int playedHours) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotCreatedException::new);
        gameService.markGameAsFinished(game, userController.getCurrentUser(), date);
        gameService.setHoursPlayedForUser(game, userController.getCurrentUser(), playedHours);
    }

    @DeleteMapping("/delete")
    public void removeGameFromDatabase(long gameId) {
        gameRepository.deleteById(gameId);
        log.info("Game with id {} deleted from database", gameId);
    }

    @PostMapping("/move/game")
    public void moveGameFromOneListToAnother(long gameId, String originalList, String targetList, boolean removeFromOriginal) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        userService.moveObjectFromListToList(userController.getCurrentUser(), game, GameList, originalList, targetList, removeFromOriginal);
    }

    @GetMapping("/list/{listName}")
    public GameListWithUserDetails getGameContentListByName(@RequestParam String listName,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                            @RequestParam(defaultValue = "id") String sortKey,
                                                            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        return userController.getCurrentUser()
                .getContentListByName(listName, GameList)
                .map(baseContentList -> detailsFounder.findDetailedGameDataFor((GameContentList) baseContentList, userController.getCurrentUser(), PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey))))
                .orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/list/{listName}")
    public GameListWithUserDetails addGameContentListToUser(@RequestParam String listName) {
        User user = userController.getCurrentUser();
        GameContentList gameContentList = userService.addNewContentListToUser(user, listName, GameList);
        return detailsFounder.findDetailedGameDataFor(gameContentList, user, PageRequest.ofSize(PAGE_SIZE));
    }

    @DeleteMapping("/list/{listName}")
    public void removeGameList(@RequestParam String listName) {
        userService.removeContentListFromUser(userController.getCurrentUser(), listName, GameList);
    }

    @PostMapping("/list/{listName}/{gameId}")
    public void addGameToUserContentList(@RequestParam String listName, @RequestParam long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        userService.addObjectToContentList(userController.getCurrentUser(), listName, GameList, game);
    }

    @DeleteMapping("/list/{listName}/{gameId}")
    public void removeGameFromList(@RequestParam String listName, @RequestParam long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        userService.removeObjectFromContentList(userController.getCurrentUser(), listName, GameList, game);
    }

    @PostMapping("/list/{currentListName}/{newListName}/{gameId}")
    public void moveGameToAnotherList(@RequestParam String currentListName, @RequestParam String newListName, @RequestParam long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(GameNotFoundException::new);
        userService.removeObjectFromContentList(userController.getCurrentUser(), currentListName, GameList, game);
        userService.addObjectToContentList(userController.getCurrentUser(), newListName, GameList, game);
    }

}
