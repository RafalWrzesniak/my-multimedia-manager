package wrzesniak.rafal.my.multimedia.manager.domain;

import java.util.Optional;

public interface ProductDetailsRepository<PRODUCT_USER_DETAILS> {

    Optional<PRODUCT_USER_DETAILS> findById(ProductUserId productUserId);

    PRODUCT_USER_DETAILS save(PRODUCT_USER_DETAILS productUserDetails);
}
