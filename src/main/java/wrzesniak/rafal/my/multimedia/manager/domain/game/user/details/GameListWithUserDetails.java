package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;

import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GAME_LIST;

@With
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameListWithUserDetails {

    String id;
    String name;
    int productsNumber;
    boolean isAllGamesList;
    ContentListType listType;
    List<GameWithUserDetailsDto> gameWithUserDetailsDtos;

    public static GameListWithUserDetails of(ContentListDynamo contentListDynamo, List<GameWithUserDetailsDto> gameDtos, int productsNumber) {
        return GameListWithUserDetails.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .isAllGamesList(contentListDynamo.isAllContentList())
                .productsNumber(productsNumber)
                .listType(GAME_LIST)
                .gameWithUserDetailsDtos(gameDtos)
                .build();
    }

}
