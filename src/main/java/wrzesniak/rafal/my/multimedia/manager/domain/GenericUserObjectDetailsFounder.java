package wrzesniak.rafal.my.multimedia.manager.domain;

import lombok.RequiredArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GenericUserObjectDetailsFounder<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> {

    private final ProductDetailsRepository<PRODUCT_USER_DETAILS> productDetailsRepository;
    private final ProductRepository<PRODUCT> productRepository;
    private final ProductUserOperations<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> productUserOperations;

    public Optional<PRODUCT_WITH_USER_DETAILS> getUserDetailsForProduct(long id, User user) {
        Optional<PRODUCT> product = productRepository.findById(id);
        return product.map(prod -> getUserDetailsForProduct(prod, user));
    }

    public PRODUCT_WITH_USER_DETAILS getUserDetailsForProduct(PRODUCT product, User user) {
        return productUserOperations.mergeProductWithUserDetails(product, getProductUserDetails(product, user));
    }

    public LIST_DETAILED_PRODUCTS getUserDetailsOfList(BaseContentList<PRODUCT> contentList, List<PRODUCT_WITH_USER_DETAILS> detailedProducts) {
        LIST_DETAILED_PRODUCTS detailedList = productUserOperations.createDetailedListFrom(contentList);
        return productUserOperations.addDetailedProductsToDetailedList(detailedList, detailedProducts);
    }

    public PRODUCT_USER_DETAILS getProductUserDetails(PRODUCT product, User user) {
        ProductUserId productUserId = productUserOperations.getProductUserIdFrom(product, user);
        return productDetailsRepository.findById(productUserId).orElseGet(() -> productUserOperations.createNewProductUserDetails(productUserId));
    }

    public void saveUserProductDetails(PRODUCT_USER_DETAILS productUserDetails) {
        productDetailsRepository.save(productUserDetails);
    }

}
