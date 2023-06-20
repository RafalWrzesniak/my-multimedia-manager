package wrzesniak.rafal.my.multimedia.manager.domain.game.service;

import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserOperations;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.GameContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.util.List;

@Component
public class GameUserOperation implements ProductUserOperations<GameWithUserDetailsDto, Game, GameUserDetails, GameListWithUserDetails> {

    @Override
    public GameWithUserDetailsDto mergeProductWithUserDetails(Game game, GameUserDetails details) {
        return GameWithUserDetailsDto.of(game, details);
    }

    @Override
    public GameListWithUserDetails createDetailedListFrom(BaseContentList<Game> contentList) {
        return GameListWithUserDetails.of((GameContentList) contentList);
    }

    @Override
    public GameListWithUserDetails addDetailedProductsToDetailedList(GameListWithUserDetails list, List<GameWithUserDetailsDto> games) {
        return list.withGameWithUserDetailsDtos(games);
    }

    @Override
    public ProductUserId getProductUserIdFrom(Game game, User user) {
        return GameUserId.of(game, user);
    }

    @Override
    public GameUserDetails createNewProductUserDetails(ProductUserId productUserId) {
        return new GameUserDetails(productUserId);
    }
}
