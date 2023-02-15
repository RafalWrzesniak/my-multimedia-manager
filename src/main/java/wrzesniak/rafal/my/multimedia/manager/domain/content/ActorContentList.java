package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;

import javax.persistence.Entity;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.ActorList;

@Entity
@NoArgsConstructor
public class ActorContentList extends BaseContentList<Actor> {

    public ActorContentList(String listName) {
        super(listName, ActorList);
    }

    public static ActorContentListBasicInfoDto toDto(ActorContentList actorContentList) {
        return ActorContentListBasicInfoDto.builder()
                .id(actorContentList.getId())
                .name(actorContentList.getName())
                .actorsNumber(actorContentList.getContentList().size())
                .listType(ActorList)
                .build();
    }

    @Value
    @Builder
    public static class ActorContentListBasicInfoDto {

        Long id;
        String name;
        int actorsNumber;
        ContentListType listType;

    }
}
