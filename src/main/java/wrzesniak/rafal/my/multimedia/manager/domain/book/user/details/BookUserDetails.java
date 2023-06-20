package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUserDetails {

    @EmbeddedId
    private BookUserId id;

    private LocalDate readOn;
    @Enumerated(EnumType.STRING)
    private BookFormat bookFormat;

    public BookUserDetails(ProductUserId productUserId) {
        this.id = new BookUserId(productUserId.getProductId(), productUserId.getUserId());
    }

}
