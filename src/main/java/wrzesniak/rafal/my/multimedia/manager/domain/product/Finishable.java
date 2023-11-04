package wrzesniak.rafal.my.multimedia.manager.domain.product;

import java.time.LocalDate;

public interface Finishable<T> {

    LocalDate getFinishedOn();

    T withFinishedOn(LocalDate localDate);

}
