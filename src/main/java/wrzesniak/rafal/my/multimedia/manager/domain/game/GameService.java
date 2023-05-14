package wrzesniak.rafal.my.multimedia.manager.domain.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.gryonline.GryOnlineService;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final WebOperations webOperations;
    private final GameRepository gameRepository;
    private final GryOnlineService gryOnlineService;
    private final GameUserDetailsRepository gameUserDetailsRepository;

    public Game createGameFromUrl(URL gryOnlineUrl, GamePlatform gamePlatform) {
        Optional<Game> gameOptional = gameRepository.findByGryOnlineUrl(gryOnlineUrl);
        if(gameOptional.isPresent()) {
            log.info("Game already exist for url: {}", gryOnlineUrl);
            return gameOptional.get();
        }
        GameDto gameDtoFromUrl = gryOnlineService.createGameDtoFromUrl(gryOnlineUrl, gamePlatform).orElseThrow(GameNotCreatedException::new);
        Game game = DtoMapper.mapToGame(gameDtoFromUrl);
        Game savedGame = gameRepository.save(game);
        webOperations.downloadImageToDirectory((gameDtoFromUrl.getImage()), savedGame.getImagePath());
        log.info("Game created from URL: {}", savedGame);
        return savedGame;
    }

    public void setPlatformForUserGame(Game game, User user, GamePlatform gamePlatform) {
        GameUserDetails gameDetails = getGameUserDetails(game, user);
        gameDetails.setGamePlatform(gamePlatform);
        log.info("Marking game `{}` as playing on {} for {}", game.getTitle(), gameDetails.getGamePlatform(), user.getUsername());
        gameUserDetailsRepository.save(gameDetails);
    }

    public void markGameAsFinished(Game game, User user, LocalDate finishPlayingDate) {
        GameUserDetails gameDetails = getGameUserDetails(game, user);
        gameDetails.setFinishedOn(firstNonNull(finishPlayingDate, LocalDate.now()));
        log.info("Marking game `{}` as finished on {} for {}", game.getTitle(), gameDetails.getFinishedOn(), user.getUsername());
        gameUserDetailsRepository.save(gameDetails);
    }

    public void setHoursPlayedForUser(Game game, User user, int playedHours) {
        GameUserDetails gameDetails = getGameUserDetails(game, user);
        gameDetails.setPlayedHours(playedHours);
        log.info("Marking game `{}` as spent on {} hours for {}", game.getTitle(), gameDetails.getPlayedHours(), user.getUsername());
        gameUserDetailsRepository.save(gameDetails);
    }

    private GameUserDetails getGameUserDetails(Game game, User user) {
        GameUserId gameUserId = GameUserId.of(game, user);
        Optional<GameUserDetails> repoDetails = gameUserDetailsRepository.findById(gameUserId);
        return repoDetails.orElse(new GameUserDetails(gameUserId));
    }
}