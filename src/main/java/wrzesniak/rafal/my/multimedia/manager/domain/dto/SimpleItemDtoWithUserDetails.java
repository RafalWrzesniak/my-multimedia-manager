package wrzesniak.rafal.my.multimedia.manager.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.RequiredArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;

@RequiredArgsConstructor
public class SimpleItemDtoWithUserDetails {

    @JsonUnwrapped
    private final Object userDetails;
    @JsonUnwrapped
    private final SimpleItem simpleItem;

}
