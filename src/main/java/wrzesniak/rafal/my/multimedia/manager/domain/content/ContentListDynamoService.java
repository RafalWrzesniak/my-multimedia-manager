package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchIdException;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentListDynamoService {

    private final DynamoDbClientGeneric<ContentListDynamo> listClient;


    public void addProductToList(SimpleItem product, String listId, String username) {
        ContentListDynamo list = getListById(listId, username);
        if(list.addItem(product)) {
            log.info("Product {} added to list {}", product.getTitle(), list.getListName());
            listClient.updateItem(list);
        } else {
            log.info("Product {} already on the list {}", product.getTitle(), list.getListName());
        }
    }

    public void addProductToAllProductsList(SimpleItem product, ContentListType contentListType, String username) {
        String listIdWithAllProducts = getListWithAllProducts(contentListType, username).getListId();
        addProductToList(product, listIdWithAllProducts, username);
    }

    public void removeProductFromList(String productId, String listId, String username) {
        ContentListDynamo list = getListById(listId, username);
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

    public ContentListDynamo createContentList(String listName, ContentListType contentListType, String username) {
        return createContentList(listName, username, contentListType, false);
    }

    public ContentListDynamo getListById(String listId, String username) {
        return listClient.getItemById(username, listId).orElseThrow(NoListWithSuchIdException::new);
    }

    public void removeContentList(String listId, String username) {
        log.info("Removing list: {}", getListById(listId, username));
        listClient.removeItem(username, listId);
    }

    public List<ContentListDynamo> findListIdsContainingProduct(String productId, ContentListType contentListType, String username) {
        return listClient.findObjectsByPartitionKey(username).stream()
                .filter(list -> contentListType.equals(list.getContentListType()))
                .filter(list -> list.contains(productId))
                .toList();
    }

    public ContentListDynamo getListWithAllProducts(ContentListType contentListType, String username) {
        return listClient.findObjectsByPartitionKey(username).stream()
                .filter(list -> contentListType.equals(list.getContentListType()))
                .filter(ContentListDynamo::isAllContentList)
                .findFirst()
                .orElseThrow();
    }

    public List<ContentListDynamo> getAllContentLists(String username) {
        return listClient.findObjectsByPartitionKey(username);
    }

}