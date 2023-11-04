package wrzesniak.rafal.my.multimedia.manager.domain.product;

import java.time.LocalDateTime;

public interface Updatable<T> {

    T withUpdatedOn(LocalDateTime dateTime);


}
