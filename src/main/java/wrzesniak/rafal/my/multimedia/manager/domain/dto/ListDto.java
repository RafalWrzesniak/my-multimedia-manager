package wrzesniak.rafal.my.multimedia.manager.domain.dto;

import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;

import java.util.List;

@Value
public class ListDto {

    String id;
    String name;
    ContentListType listType;
    List<SimpleItem> items;
    boolean isAllContentList;

    public ListDto(ContentListDynamo contentListDynamo) {
        this.id = contentListDynamo.getListId();
        this.name = contentListDynamo.getListName();
        this.listType = contentListDynamo.getContentListType();
        this.isAllContentList = contentListDynamo.isAllContentList();
        this.items = contentListDynamo.getItems();
    }

}
