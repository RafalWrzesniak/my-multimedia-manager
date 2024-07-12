package wrzesniak.rafal.my.multimedia.manager.domain.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@With
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class ContentListDynamo {

    private String listId;
    private String username;
    private String listName;
    private ContentListType contentListType;
    private LocalDateTime createdOn;
    private boolean isAllContentList;
    @ToString.Exclude
    private List<SimpleItem> items;

    public ContentListDynamo(String name, String username, ContentListType contentListType, boolean isAllContentList) {
        this.listId = UUID.randomUUID().toString();
        this.createdOn = LocalDateTime.now();
        this.username = username;
        this.listName = name;
        this.contentListType = contentListType;
        this.isAllContentList = isAllContentList;
        this.items = new ArrayList<>();
    }

    public boolean addItem(SimpleItem simpleItem) {
        boolean isNotOnList = items.stream().noneMatch(item -> item.equals(simpleItem));
        if(isNotOnList) {
            items.add(simpleItem);
            return true;
        }
        return false;
    }

    public void removeItem(String itemId) {
        items.stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .ifPresent(items::remove);
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    @DynamoDbSortKey
    @JsonProperty("id")
    public String getListId() {
        return listId;
    }

    @JsonProperty("name")
    public String getListName() {
        return listName;
    }

    @JsonProperty("listType")
    public ContentListType getContentListType() {
        return contentListType;
    }

    public boolean contains(String productId) {
        return items.stream()
                .map(SimpleItem::getId)
                .anyMatch(s -> s.equals(productId));
    }
}
