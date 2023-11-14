package wrzesniak.rafal.my.multimedia.manager.domain.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.SimpleItemDtoWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;
import wrzesniak.rafal.my.multimedia.manager.util.SimplePageRequest;

import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;


@Slf4j
@RequiredArgsConstructor
public class DefaultProductService<
        PRODUCT_WITH_USER_DETAILS,
        PRODUCT_USER_DETAILS extends ProductUserDetailsAbstract<PRODUCT_USER_DETAILS>,
        LIST_DETAILED_PRODUCTS,
        PRODUCT extends Product> {

    private final ContentListType contentListType;
    private final BiFunction<ContentListDynamo, List<PRODUCT_WITH_USER_DETAILS>, LIST_DETAILED_PRODUCTS> mergeListWithDetailedProductsFunction;

    private final UserService userService;
    private final ContentListDynamoService contentListDynamoService;
    private final ProductCreatorService<PRODUCT_WITH_USER_DETAILS> productCreatorService;
    private final DefaultDynamoRepository<PRODUCT_WITH_USER_DETAILS, PRODUCT_USER_DETAILS, PRODUCT> dynamoDbProductRepository;

    public PRODUCT_WITH_USER_DETAILS createFromUrl(URL url) {
        PRODUCT_WITH_USER_DETAILS product = productCreatorService.createProductFromUrl(url);
        Product savedProduct = dynamoDbProductRepository.getRawProductById(url.toString()).orElseThrow();
        contentListDynamoService.addProductToAllProductsList(SimpleItem.of(savedProduct), contentListType);
        return product;
    }

    public void addProductToList(String productId, String listId) {
        Product product = dynamoDbProductRepository.getRawProductById(productId).orElseThrow();
        contentListDynamoService.addProductToList(SimpleItem.of(product), listId);
    }

    public List<LIST_DETAILED_PRODUCTS> findListsContainingProduct(String productId) {
        return contentListDynamoService.findListIdsContainingProduct(productId, contentListType).stream()
                .map(dynamoList -> mergeListWithDetailedProductsFunction.apply(dynamoList, List.of()))
                .toList();
    }

    public void markProductAsFinished(String productId, LocalDate finishDate) {
        LocalDate realFinishDate = Optional.ofNullable(finishDate).orElse(LocalDate.now());
        PRODUCT_USER_DETAILS productUserDetails = getProductUserDetails(productId);
        PRODUCT_USER_DETAILS updatedDetails = productUserDetails.withFinishedOn(realFinishDate);
        log.info("Marking {} as finished on {} for {}", productId, realFinishDate, userService.getCurrentUsername());
        updateUserProductDetails(updatedDetails);
    }

    public Optional<PRODUCT_WITH_USER_DETAILS> getById(String id) {
        Optional<PRODUCT> product = dynamoDbProductRepository.getRawProductById(id);
        PRODUCT_USER_DETAILS productUserDetails = dynamoDbProductRepository.getProductUserDetails(id);
        return product.map(prod -> dynamoDbProductRepository.mergeProductWithDetails(prod, productUserDetails));
    }

    public List<PRODUCT_WITH_USER_DETAILS> findByPropertyName(String listId, String propertyName, String propertyValue, SimplePageRequest pageRequest) {
        List<PRODUCT_WITH_USER_DETAILS> allProducts = getAllProductsForList(listId);
        return allProducts.stream()
                .filter(product -> productPropertyContains(product, propertyName, propertyValue))
                .skip((long) pageRequest.pageSize() * pageRequest.page())
                .limit(pageRequest.pageSize())
                .toList();
    }

    public List<PRODUCT_WITH_USER_DETAILS> findLastFinished(int numberOfPositions) {
        return dynamoDbProductRepository.findRecentlyDone(numberOfPositions);
    }

    public ContentListDynamo getListById(String listId) {
        return contentListDynamoService.getListById(listId);
    }

    public LIST_DETAILED_PRODUCTS getListById(String listId, SimplePageRequest pageRequest) {
        ContentListDynamo list = contentListDynamoService.getListById(listId);
        List<PRODUCT_WITH_USER_DETAILS> allProducts = getAllProductsForList(listId);
        List<PRODUCT_WITH_USER_DETAILS> products = allProducts.stream()
                .sorted((obj1, obj2) -> compareProducts(pageRequest, obj1, obj2))
                .skip((long) pageRequest.pageSize() * pageRequest.page())
                .limit(pageRequest.pageSize())
                .toList();
        return mergeListWithDetailedProductsFunction.apply(list, products);
    }

    public List<SimpleItemDtoWithUserDetails> getDetailsForItems(List<SimpleItem> simpleItems) {
        return simpleItems.parallelStream()
                .map(simpleItem -> new SimpleItemDtoWithUserDetails(dynamoDbProductRepository.getProductUserDetails(simpleItem.getId()), simpleItem))
                .toList();
    }

    public ContentListDynamo createContentList(String listName) {
        ContentListDynamo createdList = contentListDynamoService.createContentList(listName, contentListType);
        return getListById(createdList.getListId());
    }

    public void removeContentList(String listId) {
        contentListDynamoService.removeContentList(listId);
    }

    public void removeProductFromContentList(String productId, String listId) {
        contentListDynamoService.removeProductFromList(productId, listId);
    }

    protected PRODUCT_USER_DETAILS getProductUserDetails(String productId) {
        return dynamoDbProductRepository.getProductUserDetails(productId);
    }

    protected void updateUserProductDetails(PRODUCT_USER_DETAILS userDetails) {
        dynamoDbProductRepository.updateUserDetails(userDetails);
    }

    private boolean productPropertyContains(PRODUCT_WITH_USER_DETAILS product, String propertyName, String propertyValue) {
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
            return pageRequest.direction().equals("ASC") ? value1.compareTo(value2) : value2.compareTo(value1);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            return 0;
        }
    }

    private List<PRODUCT_WITH_USER_DETAILS> getAllProductsForList(String listId) {
        return contentListDynamoService.getListById(listId).getItems().parallelStream()
                .map(SimpleItem::getId)
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

}
