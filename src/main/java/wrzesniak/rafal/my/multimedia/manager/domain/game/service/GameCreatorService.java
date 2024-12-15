package wrzesniak.rafal.my.multimedia.manager.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.GameNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.gryonline.GryOnlineUrl;
import wrzesniak.rafal.my.multimedia.manager.web.gryonline.GryOnlineService;

import java.net.URL;

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
        return gryOnlineService.createGameDtoFromUrl(gryOnlineUrl, gamePlatform)
                .map(DtoMapper::mapToGame)
                .map(gameDynamo -> gameDynamoRepository.saveProduct(gameDynamo, username))
                .orElseThrow(GameNotCreatedException::new);
    }

}
