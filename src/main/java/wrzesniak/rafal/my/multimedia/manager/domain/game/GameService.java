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
        GameUserId gameUserId = GameUserId.of(game, user);
        Optional<GameUserDetails> repoDetails = gameUserDetailsRepository.findById(gameUserId);
        GameUserDetails gameDetails = repoDetails.orElse(new GameUserDetails(gameUserId));
        gameDetails.setGamePlatform(gamePlatform);
        gameUserDetailsRepository.save(gameDetails);
    }

    public void markGameAsFinished(Game game, User user, LocalDate finishPlayingDate) {
        GameUserId gameUserId = GameUserId.of(game, user);
        Optional<GameUserDetails> repoDetails = gameUserDetailsRepository.findById(gameUserId);
        GameUserDetails gameDetails = repoDetails.orElse(new GameUserDetails(gameUserId));
        gameDetails.setFinishedOn(finishPlayingDate);
        gameUserDetailsRepository.save(gameDetails);
    }
}
