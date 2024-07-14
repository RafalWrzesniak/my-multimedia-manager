package wrzesniak.rafal.my.multimedia.manager.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.gryonline.GryOnlineUrl;
import wrzesniak.rafal.my.multimedia.manager.web.gryonline.GryOnlineService;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GameCreatorService implements ProductCreatorService<GameWithUserDetailsDto> {

    private final DefaultDynamoRepository<GameWithUserDetailsDto, GameUserDetailsDynamo, GameDynamo> gameDynamoRepository;
    private final GryOnlineService gryOnlineService;

    @Override
    public GameWithUserDetailsDto createProductFromUrl(URL gryOnlineUrl, String username) {
        return createGameFromUrl(gryOnlineUrl, null, username);
    }

    public GameWithUserDetailsDto createGameFromUrl(@GryOnlineUrl URL gryOnlineUrl, GamePlatform gamePlatform, String username) {
        Optional<GameWithUserDetailsDto> gameInDatabase = gameDynamoRepository.getById(gryOnlineUrl.toString(), username);
        if(gameInDatabase.isPresent()) {
            log.info("Game already exist for url: {}", gryOnlineUrl);
            gameDynamoRepository.createOrUpdateUserDetailsFor(gryOnlineUrl.toString(), username);
            return gameInDatabase.get();
        }
        GameDto gameDtoFromUrl = gryOnlineService.createGameDtoFromUrl(gryOnlineUrl, gamePlatform).orElseThrow(GameNotCreatedException::new);
        GameDynamo game = DtoMapper.mapToGame(gameDtoFromUrl);
        GameWithUserDetailsDto savedGame = gameDynamoRepository.saveProduct(game, username);
        log.info("Game created from URL: {}", savedGame);
        return savedGame;
    }

}
