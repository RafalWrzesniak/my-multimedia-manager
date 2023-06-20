package wrzesniak.rafal.my.multimedia.manager.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.repository.GameRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.gryonline.GryOnlineService;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameCreatorService implements ProductCreatorService<Game> {

    private final WebOperations webOperations;
    private final GameRepository gameRepository;
    private final GryOnlineService gryOnlineService;

    @Override
    public Game createProductFromUrl(URL gryOnlineUrl) {
        return createGameFromUrl(gryOnlineUrl, null);
    }

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

}
