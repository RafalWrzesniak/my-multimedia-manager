package wrzesniak.rafal.my.multimedia.manager.domain;

import java.net.URL;

public interface ProductCreatorService<PRODUCT> {

    PRODUCT createProductFromUrl(URL url);

}
