package wrzesniak.rafal.my.multimedia.manager.domain.dto;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;

import java.util.List;

@Value
@Builder
public class ListDto {

    String id;
    String name;
    ContentListType listType;
    List<SimpleItemDtoWithUserDetails> items;
    boolean isAllContentList;

    public static ListDto of(ContentListDynamo contentListDynamo, List<SimpleItemDtoWithUserDetails> detailedList) {
        return ListDto.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .listType(contentListDynamo.getContentListType())
                .isAllContentList(contentListDynamo.isAllContentList())
                .items(detailedList)
                .build();
    }

}
