package wrzesniak.rafal.my.multimedia.manager.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.google.common.base.MoreObjects.firstNonNull;

@Slf4j
@RequiredArgsConstructor
public class DefaultProductService<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> {

    private final UserService userService;
    private final ProductRepository<PRODUCT> productRepository;
    private final GenericUserObjectDetailsFounder<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> genericUserObjectDetailsFounder;
    private final ContentListType contentListType;
    private final Function<PRODUCT_WITH_USER_DETAILS, LocalDate> getFinishedOn;
    private final ProductCreatorService<PRODUCT> productCreatorService;
    private final BiFunction<PRODUCT_USER_DETAILS, LocalDate, PRODUCT_USER_DETAILS> setFinishedOn;

    public PRODUCT createFromUrl(URL url) {
        PRODUCT product = productCreatorService.createProductFromUrl(url);
        addProductToList(product, contentListType.getAllProductsListName());
        return product;
    }

    public void addProductToList(long productId, String listName) {
        PRODUCT product = productRepository.findById(productId).orElseThrow();
        addProductToList(product, listName);
    }

    public void addProductToList(PRODUCT product, String listName) {
        userService.addObjectToContentList(userService.getCurrentUser(), listName, contentListType, product);
    }

    public void markProductAsFinished(long productId, LocalDate finishDate) {
        PRODUCT product = productRepository.findById(productId).orElseThrow();
        PRODUCT_USER_DETAILS productUserDetails = genericUserObjectDetailsFounder.getProductUserDetails(product, userService.getCurrentUser());
        LocalDate realFinishDate = firstNonNull(finishDate, LocalDate.now());
        PRODUCT_USER_DETAILS updatedDetails = setFinishedOn.apply(productUserDetails, realFinishDate);
        log.info("Marking {} as finished on {} for {}", product, realFinishDate, userService.getCurrentUser().getUsername());
        genericUserObjectDetailsFounder.saveUserProductDetails(updatedDetails);
    }

    public Optional<PRODUCT_WITH_USER_DETAILS> findById(long id) {
        return genericUserObjectDetailsFounder.getUserDetailsForProduct(id, userService.getCurrentUser());
    }

    public List<PRODUCT_WITH_USER_DETAILS> findByPropertyName(String propertyName, String propertyValue) {
        List<PRODUCT> contentList = getAllUserProducts();
        Specification<PRODUCT> likeIgnoreCase = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(propertyName)), "%" + propertyValue.toLowerCase() + "%");

        return productRepository.findAll(likeIgnoreCase).stream()
                .filter(contentList::contains)
                .map(this::mapToProductWithUserDetails)
                .toList();
    }

    public List<PRODUCT_WITH_USER_DETAILS> findAllUserProducts(Pageable pageRequest) {
        return getAllUserProducts(pageRequest).stream()
                .map(this::mapToProductWithUserDetails)
                .toList();
    }

    public List<PRODUCT_WITH_USER_DETAILS> findLastFinished(int numberOfPositions) {
        return getAllUserProducts().stream()
                .map(this::mapToProductWithUserDetails)
                .filter(product -> Objects.nonNull(getFinishedOn.apply(product)))
                .sorted(Comparator.comparing(getFinishedOn).reversed())
                .limit(numberOfPositions)
                .toList();
    }

    public LIST_DETAILED_PRODUCTS getContentListByName(String listName, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        BaseContentList<PRODUCT> list = (BaseContentList<PRODUCT>) currentUser.getContentListByName(listName, contentListType).orElseThrow();
        List<PRODUCT_WITH_USER_DETAILS> detailedProducts = productRepository.findProductsInContentList(list.getId(), pageable).stream()
                .map(this::mapToProductWithUserDetails)
                .toList();
        return genericUserObjectDetailsFounder.getUserDetailsOfList(list, detailedProducts);
    }

    public LIST_DETAILED_PRODUCTS createContentList(String listName) {
        User user = userService.getCurrentUser();
        BaseContentList<PRODUCT> baseContentList = userService.addNewContentListToUser(user, listName, contentListType);
        return genericUserObjectDetailsFounder.getUserDetailsOfList(baseContentList, List.of());
    }

    public void removeContentList(String listName) {
        userService.removeContentListFromUser(userService.getCurrentUser(), listName, contentListType);
    }

    public void removeProductFromContentList(long productId, String listName) {
        Optional<PRODUCT> productOptional = productRepository.findById(productId);
        productOptional.ifPresent(product -> userService.removeObjectFromContentList(userService.getCurrentUser(), listName, contentListType, product));
    }

    public void moveProductToAnotherList(List<Long> productIds, String originListName, String targetListName, boolean removeFromOrigin) {
        productIds.stream()
                .map(productRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(product -> userService.moveObjectFromListToList(userService.getCurrentUser(), product, contentListType, originListName, targetListName, removeFromOrigin));
    }

    public void removeProductFromDatabase(long id) {
        productRepository.deleteById(id);
    }

    protected PRODUCT_USER_DETAILS getProductUserDetails(PRODUCT product) {
        return genericUserObjectDetailsFounder.getProductUserDetails(product, userService.getCurrentUser());
    }

    protected void saveUserProductDetails(PRODUCT_USER_DETAILS gameDetails) {
        genericUserObjectDetailsFounder.saveUserProductDetails(gameDetails);
    }

    private List<PRODUCT> getAllUserProducts() {
        return getAllUserProducts(null);
    }

    private List<PRODUCT> getAllUserProducts(Pageable pageRequest) {
        Long allProductsListId = userService.getCurrentUser().getContentListByName(contentListType.getAllProductsListName(), contentListType).orElseThrow().getId();
        return productRepository.findProductsInContentList(allProductsListId, pageRequest);
    }

    protected Optional<PRODUCT> findRawProductById(long productId) {
        return productRepository.findById(productId);
    }

    protected PRODUCT_WITH_USER_DETAILS mapToProductWithUserDetails(PRODUCT product) {
        return genericUserObjectDetailsFounder.getUserDetailsForProduct(product, userService.getCurrentUser());
    }
}
