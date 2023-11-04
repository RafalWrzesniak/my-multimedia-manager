package wrzesniak.rafal.my.multimedia.manager.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsDtoDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.web.gryonline.GryOnlineService;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameCreatorService implements ProductCreatorService<GameWithUserDetailsDto> {

    private final DefaultDynamoRepository<GameWithUserDetailsDto, GameUserDetailsDtoDynamo, GameDynamo> gameDynamoRepository;
    private final GryOnlineService gryOnlineService;

    @Override
    public GameWithUserDetailsDto createProductFromUrl(URL gryOnlineUrl) {
        return createGameFromUrl(gryOnlineUrl, null);
    }

    public GameWithUserDetailsDto createGameFromUrl(URL gryOnlineUrl, GamePlatform gamePlatform) {
        Optional<GameWithUserDetailsDto> gameInDatabase = gameDynamoRepository.getById(gryOnlineUrl.toString());
        if(gameInDatabase.isPresent()) {
            log.info("Game already exist for url: {}", gryOnlineUrl);
            gameDynamoRepository.createOrUpdateUserDetailsFor(gryOnlineUrl.toString());
            return gameInDatabase.get();
        }
        GameDto gameDtoFromUrl = gryOnlineService.createGameDtoFromUrl(gryOnlineUrl, gamePlatform).orElseThrow(GameNotCreatedException::new);
        GameDynamo game = DtoMapper.mapToGame(gameDtoFromUrl);
        GameWithUserDetailsDto savedGame = gameDynamoRepository.saveProduct(game);
        log.info("Game created from URL: {}", savedGame);
        return savedGame;
    }

}
