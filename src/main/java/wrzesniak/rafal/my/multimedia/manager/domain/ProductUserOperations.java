package wrzesniak.rafal.my.multimedia.manager.domain;

import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.util.List;

public interface ProductUserOperations<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> {

    PRODUCT_WITH_USER_DETAILS mergeProductWithUserDetails(PRODUCT product, PRODUCT_USER_DETAILS productUserDetails);

    LIST_DETAILED_PRODUCTS createDetailedListFrom(BaseContentList<PRODUCT> contentList);

    LIST_DETAILED_PRODUCTS addDetailedProductsToDetailedList(LIST_DETAILED_PRODUCTS list, List<PRODUCT_WITH_USER_DETAILS> products);

    ProductUserId getProductUserIdFrom(PRODUCT product, User user);

    PRODUCT_USER_DETAILS createNewProductUserDetails(ProductUserId productUserId);
}
