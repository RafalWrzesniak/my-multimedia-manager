package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.content.GameContentList;

import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GAME_LIST;

@With
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameListWithUserDetails {

    String id;
    String name;
    int gamesNumber;
    boolean isAllGamesList;
    ContentListType listType;
    List<GameWithUserDetailsDto> gameWithUserDetailsDtos;

    public static GameListWithUserDetails of(ContentListDynamo contentListDynamo, List<GameWithUserDetailsDto> gameDtos) {
        return GameListWithUserDetails.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .isAllGamesList(contentListDynamo.isAllContentList())
                .gamesNumber(gameDtos.size())
                .listType(GAME_LIST)
                .gameWithUserDetailsDtos(gameDtos)
                .build();
    }

    public static GameListWithUserDetails of(GameContentList gameContentList) {
        return GameListWithUserDetails.builder()
                .id(gameContentList.getId().toString())
                .name(gameContentList.getName())
                .isAllGamesList(gameContentList.isAllContentList())
                .gamesNumber(gameContentList.getContentList().size())
                .listType(GAME_LIST)
                .build();
    }

}
