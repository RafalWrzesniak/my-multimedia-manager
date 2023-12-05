package wrzesniak.rafal.my.multimedia.manager.domain.product;

import java.net.URL;

public interface ProductCreatorService<PRODUCT_WITH_USER_DETAILS> {

    PRODUCT_WITH_USER_DETAILS createProductFromUrl(URL url, String username);

}
