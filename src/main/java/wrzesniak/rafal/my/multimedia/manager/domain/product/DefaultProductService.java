package wrzesniak.rafal.my.multimedia.manager.domain.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.SimpleItemDtoWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.util.SimplePageRequest;
import wrzesniak.rafal.my.multimedia.manager.util.TriFunction;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
public class DefaultProductService<
        PRODUCT_WITH_USER_DETAILS,
        PRODUCT_USER_DETAILS extends ProductUserDetailsAbstract<PRODUCT_USER_DETAILS>,
        LIST_DETAILED_PRODUCTS,
        PRODUCT extends Product> {

    private final ContentListType contentListType;
    private final TriFunction<ContentListDynamo, List<PRODUCT_WITH_USER_DETAILS>, Integer, LIST_DETAILED_PRODUCTS> mergeListWithDetailedProductsFunction;

    private final ContentListDynamoService contentListDynamoService;
    private final ProductCreatorService<PRODUCT_WITH_USER_DETAILS> productCreatorService;
    private final DefaultDynamoRepository<PRODUCT_WITH_USER_DETAILS, PRODUCT_USER_DETAILS, PRODUCT> dynamoDbProductRepository;

    public PRODUCT_WITH_USER_DETAILS createFromUrl(URL url, String username) {
        PRODUCT_WITH_USER_DETAILS product = productCreatorService.createProductFromUrl(url, username);
        Product savedProduct = dynamoDbProductRepository.getRawProductById(url.toString()).orElseThrow();
        contentListDynamoService.addProductToAllProductsList(SimpleItem.of(savedProduct), contentListType, username);
        return product;
    }

    public void addProductToList(String productId, String listId, String username) {
        Product product = dynamoDbProductRepository.getRawProductById(productId).orElseThrow();
        contentListDynamoService.addProductToList(SimpleItem.of(product), listId, username);
    }

    public List<LIST_DETAILED_PRODUCTS> findListsContainingProduct(String productId, String username) {
        return contentListDynamoService.findListIdsContainingProduct(productId, contentListType, username).stream()
                .map(dynamoList -> mergeListWithDetailedProductsFunction.apply(dynamoList, List.of(), 0))
                .toList();
    }

    public void markProductAsFinished(String productId, LocalDate finishDate, String username) {
        LocalDate realFinishDate = Optional.ofNullable(finishDate).orElse(LocalDate.now());
        PRODUCT_USER_DETAILS productUserDetails = getProductUserDetails(productId, username);
        PRODUCT_USER_DETAILS updatedDetails = productUserDetails.withFinishedOn(realFinishDate);
        log.info("Marking {} as finished on {} for {}", productId, realFinishDate, username);
        updateUserProductDetails(updatedDetails, username);
    }

    public Optional<PRODUCT_WITH_USER_DETAILS> getById(String id, String username) {
        log.info("Getting information about product with id: {} for username: {}", id, username);
        Optional<PRODUCT> product = dynamoDbProductRepository.getRawProductById(id);
        PRODUCT_USER_DETAILS productUserDetails = dynamoDbProductRepository.getProductUserDetails(id, username);
        return product.map(prod -> dynamoDbProductRepository.mergeProductWithDetails(prod, productUserDetails));
    }

    public LIST_DETAILED_PRODUCTS findListProductsWithRequest(String listId, String propertyName, String propertyValue, SimplePageRequest pageRequest, String username) {
        ContentListDynamo list = contentListDynamoService.getListById(listId, username);
        List<PRODUCT_WITH_USER_DETAILS> allProducts = getAllProductsForList(listId, username);
        List<PRODUCT_WITH_USER_DETAILS> foundAndSortedProducts = allProducts.stream()
                .filter(product -> productPropertyContains(product, propertyName, propertyValue))
                .sorted((obj1, obj2) -> compareProducts(pageRequest, obj1, obj2))
                .toList();
        List<PRODUCT_WITH_USER_DETAILS> pagedProducts = foundAndSortedProducts.stream()
                .skip((long) pageRequest.pageSize() * pageRequest.page())
                .limit(pageRequest.pageSize())
                .toList();
        return mergeListWithDetailedProductsFunction.apply(list, pagedProducts, foundAndSortedProducts.size());
    }

    public List<PRODUCT_WITH_USER_DETAILS> findLastFinished(int numberOfPositions, String username, String controllerType) {
        return dynamoDbProductRepository.findRecentlyDone(numberOfPositions, username, controllerType);
    }

    public ContentListDynamo getListById(String listId, String username) {
        return contentListDynamoService.getListById(listId, username);
    }

    public List<SimpleItemDtoWithUserDetails> getDetailsForItems(List<SimpleItem> simpleItems, String username) {
        return simpleItems.parallelStream()
                .map(simpleItem -> new SimpleItemDtoWithUserDetails(dynamoDbProductRepository.getProductUserDetails(simpleItem.getId(), username),
                        simpleItem.withTitle(URLDecoder.decode(simpleItem.getDisplayedTitle(), StandardCharsets.UTF_8))))
                .toList();
    }

    public ContentListDynamo createContentList(String listName, String username) {
        ContentListDynamo createdList = contentListDynamoService.createContentList(listName, contentListType, username);
        return getListById(createdList.getListId(), username);
    }

    public void removeContentList(String listId, String username) {
        contentListDynamoService.removeContentList(listId, username);
    }

    public void removeProductFromContentList(String productId, String listId, String username) {
        contentListDynamoService.removeProductFromList(productId, listId, username);
    }

    protected PRODUCT_USER_DETAILS getProductUserDetails(String productId, String username) {
        return dynamoDbProductRepository.getProductUserDetails(productId, username);
    }

    protected void updateUserProductDetails(PRODUCT_USER_DETAILS userDetails, String username) {
        dynamoDbProductRepository.updateUserDetails(userDetails, username);
    }

    private boolean productPropertyContains(PRODUCT_WITH_USER_DETAILS product, String propertyName, String propertyValue) {
        if(propertyName == null && propertyValue == null) {
            return true;
        }
        try {
            Field field = product.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(product).toString().toLowerCase().contains(propertyValue.toLowerCase());
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            return false;
        }
    }

    private int compareProducts(SimplePageRequest pageRequest, PRODUCT_WITH_USER_DETAILS obj1, PRODUCT_WITH_USER_DETAILS obj2) {
        try {
            Field field = obj1.getClass().getDeclaredField(pageRequest.sortKey());
            field.setAccessible(true);
            Comparable value1 = (Comparable) field.get(obj1);
            Comparable value2 = (Comparable) field.get(obj2);
            if(value1 == null && value2 == null) {
                return 0;
            }
            if(value1 == null) {
                return 1;
            } else if(value2 == null) {
                return -1;
            }
            return pageRequest.direction().equals("ASC") ? value1.compareTo(value2) : value2.compareTo(value1);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            return 0;
        }
    }

    private List<PRODUCT_WITH_USER_DETAILS> getAllProductsForList(String listId, String username) {
        return contentListDynamoService.getListById(listId, username).getItems().parallelStream()
                .map(SimpleItem::getId)
                .map((String id) -> getById(id, username))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void renameList(String listId, String newListName, String username) {
        log.info("Renaming list to name {}. List id: {}", newListName, listId);
        contentListDynamoService.renameList(listId, newListName, username);
    }
}
