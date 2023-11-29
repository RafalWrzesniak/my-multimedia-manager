package wrzesniak.rafal.my.multimedia.manager.domain.dynamodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Finishable;
import wrzesniak.rafal.my.multimedia.manager.domain.product.IdSupport;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Updatable;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static wrzesniak.rafal.my.multimedia.manager.config.CacheConfiguration.*;

@Slf4j
@RequiredArgsConstructor
public class DefaultDynamoRepository<
        PRODUCT_WITH_USER_DETAILS,
        PRODUCT_USER_DETAILS extends Finishable<PRODUCT_USER_DETAILS> & IdSupport & Updatable<PRODUCT_USER_DETAILS>,
        PRODUCT extends Product> {

    private final DynamoDbClientGeneric<PRODUCT> productDynamoClient;
    private final DynamoDbClientGeneric<PRODUCT_USER_DETAILS> productUserDetailsDynamoClient;
    private final UserService userService;
    private final BiFunction<String, String, PRODUCT_USER_DETAILS> createNewUserDetailsFunction;
    private final BiFunction<PRODUCT, PRODUCT_USER_DETAILS, PRODUCT_WITH_USER_DETAILS> mergeProductWithUserDetails;

    public PRODUCT_WITH_USER_DETAILS saveProduct(PRODUCT product) {
        productDynamoClient.saveItem(product);
        createOrUpdateUserDetailsFor(product.getId());
        return getById(product.getId()).orElseThrow(() -> new IllegalStateException("Cannot find product that should have been saved: " + product));
    }

    public void createOrUpdateUserDetailsFor(String productId) {
        PRODUCT_USER_DETAILS userDetails = productUserDetailsDynamoClient.getItemById(owner(), productId)
                .map(userDetails1 -> userDetails1.withUpdatedOn(LocalDateTime.now()))
                .orElse(createNewUserDetailsFunction.apply(owner(), productId));
        productUserDetailsDynamoClient.saveItem(userDetails);
    }

    public Optional<PRODUCT_WITH_USER_DETAILS> getById(String productId) {
        Optional<PRODUCT> product = productDynamoClient.getItemById(productId);
        PRODUCT_USER_DETAILS userDetails = getProductUserDetails(productId);
        return product.map(prod -> mergeProductWithUserDetails.apply(prod, userDetails));
    }

    public PRODUCT_WITH_USER_DETAILS mergeProductWithDetails(PRODUCT product, PRODUCT_USER_DETAILS userDetails) {
        return mergeProductWithUserDetails.apply(product, userDetails);
    }

    @Cacheable(value = RECENTLY_DONE_CACHE)
    public List<PRODUCT_WITH_USER_DETAILS> findRecentlyDone(int limit) {
        return productUserDetailsDynamoClient.findObjectsByPartitionKey(owner()).stream()
                .filter(userDetails -> userDetails.getFinishedOn() != null)
                .sorted((m1, m2) -> m2.getFinishedOn().compareTo(m1.getFinishedOn()))
                .limit(limit)
                .map(userDetails -> getById(userDetails.getId()))
                .map(Optional::get)
                .toList();
    }

    @Caching(cacheable = {
            @Cacheable(value = BOOK_USER_DETAILS_CACHE, key = "#productId", condition="#productId.contains('lubimyczytac')"),
            @Cacheable(value = GAME_USER_DETAILS_CACHE, key="#productId", condition="#productId.contains('gry-online')"),
            @Cacheable(value = MOVIE_USER_DETAILS_CACHE, key="#productId", condition="#productId.contains('filmweb')")})
    public PRODUCT_USER_DETAILS getProductUserDetails(String productId) {
        return getProductUserDetails(productId, owner());
    }

    public PRODUCT_USER_DETAILS getProductUserDetails(String productId, String username) {
        log.info("Getting product user details for product {}", productId);
        return productUserDetailsDynamoClient.getItemById(username, productId)
                .orElse(createNewUserDetailsFunction.apply(username, productId));
    }

    @Caching(put = {
            @CachePut(value = BOOK_USER_DETAILS_CACHE, key = "#productUserDetails.getId()", condition = "#productUserDetails.getId().contains('lubimyczytac')"),
            @CachePut(value = GAME_USER_DETAILS_CACHE, key = "#productUserDetails.getId()", condition = "#productUserDetails.getId().contains('gry-online')"),
            @CachePut(value = MOVIE_USER_DETAILS_CACHE, key = "#productUserDetails.getId()", condition = "#productUserDetails.getId().contains('filmweb')")})
    public PRODUCT_USER_DETAILS updateUserDetails(PRODUCT_USER_DETAILS productUserDetails) {
        log.info("Updating product user details {}", productUserDetails);
        productUserDetailsDynamoClient.updateItem(productUserDetails.withUpdatedOn(LocalDateTime.now()));
        return productUserDetails;
    }

    @Caching(cacheable = {
            @Cacheable(value = BOOK_DETAILS_CACHE, key = "#productId", condition="#productId.contains('lubimyczytac')"),
            @Cacheable(value = GAME_DETAILS_CACHE, key="#productId", condition="#productId.contains('gry-online')"),
            @Cacheable(value = MOVIE_DETAILS_CACHE, key="#productId", condition="#productId.contains('filmweb')")})
    public Optional<PRODUCT> getRawProductById(String productId) {
        log.info("Getting product details for product {}", productId);
        return productDynamoClient.getItemById(productId);
    }

    private String owner() {
        return userService.getCurrentUsername();
    }

}
