package wrzesniak.rafal.my.multimedia.manager.domain.dynamodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductUserDetailsAbstract;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@RequiredArgsConstructor
public class DefaultDynamoRepository<
        PRODUCT_WITH_USER_DETAILS,
        PRODUCT_USER_DETAILS extends ProductUserDetailsAbstract<PRODUCT_USER_DETAILS>,
        PRODUCT extends Product> {

    private final DynamoDbClientGeneric<PRODUCT> productDynamoClient;
    private final DynamoDbClientGeneric<PRODUCT_USER_DETAILS> productUserDetailsDynamoClient;
    private final BiFunction<String, String, PRODUCT_USER_DETAILS> createNewUserDetailsFunction;
    private final BiFunction<PRODUCT, PRODUCT_USER_DETAILS, PRODUCT_WITH_USER_DETAILS> mergeProductWithUserDetails;

    public PRODUCT_WITH_USER_DETAILS saveProduct(PRODUCT product, String username) {
        productDynamoClient.saveItem(product);
        createOrUpdateUserDetailsFor(product.getId(), username);
        return getById(product.getId(), username).orElseThrow(() -> new IllegalStateException("Cannot find product that should have been saved: " + product));
    }

    public void createOrUpdateUserDetailsFor(String productId, String username) {
        PRODUCT_USER_DETAILS userDetails = productUserDetailsDynamoClient.getItemById(username, productId)
                .map(userDetails1 -> userDetails1.withUpdatedOn(LocalDateTime.now()))
                .orElse(createNewUserDetailsFunction.apply(username, productId));
        productUserDetailsDynamoClient.saveItem(userDetails);
    }

    public Optional<PRODUCT_WITH_USER_DETAILS> getById(String productId, String username) {
        Optional<PRODUCT> product = productDynamoClient.getItemById(productId);
        PRODUCT_USER_DETAILS userDetails = getProductUserDetails(productId, username);
        return product.map(prod -> mergeProductWithUserDetails.apply(prod, userDetails));
    }

    public PRODUCT_WITH_USER_DETAILS mergeProductWithDetails(PRODUCT product, PRODUCT_USER_DETAILS userDetails) {
        return mergeProductWithUserDetails.apply(product, userDetails);
    }

    public List<PRODUCT_WITH_USER_DETAILS> findRecentlyDone(int limit, String username, String controllerType) {
        log.info("Finding {} recently done for {}", controllerType, username);
        return productUserDetailsDynamoClient.findObjectsByPartitionKey(username).stream()
                .filter(userDetails -> userDetails.getFinishedOn() != null)
                .sorted((m1, m2) -> m2.getFinishedOn().compareTo(m1.getFinishedOn()))
                .limit(limit)
                .map(userDetails -> getById(userDetails.getId(), username))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public PRODUCT_USER_DETAILS getProductUserDetails(String productId, String username) {
        return productUserDetailsDynamoClient.getItemById(username, productId)
                .orElse(createNewUserDetailsFunction.apply(username, productId));
    }

    public PRODUCT_USER_DETAILS getProductUserDetailsWithoutCache(String productId, String username) {
        return productUserDetailsDynamoClient.getItemById(username, productId)
            .orElse(createNewUserDetailsFunction.apply(username, productId));
    }

    public PRODUCT_USER_DETAILS updateUserDetails(PRODUCT_USER_DETAILS productUserDetails, String username) {
        log.info("Updating product user details {}", productUserDetails);
        productUserDetailsDynamoClient.updateItem(productUserDetails.withUpdatedOn(LocalDateTime.now()));
        return productUserDetails;
    }

    public Optional<PRODUCT> getRawProductById(String productId) {
        return productDynamoClient.getItemById(productId);
    }

}
