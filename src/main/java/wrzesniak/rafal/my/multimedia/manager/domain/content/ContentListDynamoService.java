package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentListDynamoService {

    private final UserService userService;
    private final DynamoDbClientGeneric<ContentListDynamo> listClient;


    public void addProductToList(SimpleItem product, String listId) {
        ContentListDynamo list = getListById(listId);
        if(list.addItem(product)) {
            log.info("Product {} added to list {}", product.getTitle(), list.getListName());
            listClient.updateItem(list);
        } else {
            log.info("Product {} already on the list {}", product.getTitle(), list.getListName());
        }
    }

    public void addProductToAllProductsList(SimpleItem product, ContentListType contentListType) {
        String listIdWithAllProducts = getListWithAllProducts(contentListType).getListId();
        addProductToList(product, listIdWithAllProducts);
    }

    public void removeProductFromList(String productId, String listId) {
        ContentListDynamo list = getListById(listId);
        list.removeItem(productId);
        log.info("Product {} removed from list {}", productId, list.getListName());
        listClient.updateItem(list);
    }

    public ContentListDynamo createContentList(String listName, String username, ContentListType contentListType, boolean isAllContentList) {
        ContentListDynamo contentListDynamo = new ContentListDynamo(listName, username, contentListType)
                .withAllContentList(isAllContentList);
        listClient.saveItem(contentListDynamo);
        log.info("Created new list {}", contentListDynamo);
        return contentListDynamo;
    }

    public ContentListDynamo createContentList(String listName, ContentListType contentListType) {
        return createContentList(listName, userService.getCurrentUsername(), contentListType, false);
    }

    public ContentListDynamo getListById(String listId) {
        return listClient.getItemById(userService.getCurrentUsername(), listId).orElseThrow();
    }

    public void removeContentList(String listId) {
        log.info("Removing list: {}", getListById(listId));
        listClient.removeItem(userService.getCurrentUsername(), listId);
    }

    public List<ContentListDynamo> findListIdsContainingProduct(String productId, ContentListType contentListType) {
        return listClient.findObjectsByPartitionKey(userService.getCurrentUsername()).stream()
                .filter(list -> contentListType.equals(list.getContentListType()))
                .filter(list -> list.contains(productId))
                .toList();
    }

    public ContentListDynamo getListWithAllProducts(ContentListType contentListType) {
        return listClient.findObjectsByPartitionKey(userService.getCurrentUsername()).stream()
                .filter(list -> contentListType.equals(list.getContentListType()))
                .filter(ContentListDynamo::isAllContentList)
                .findFirst()
                .orElseThrow();
    }

    public List<ContentListDynamo> getAllContentLists() {
        return listClient.findObjectsByPartitionKey(userService.getCurrentUsername());
    }

}