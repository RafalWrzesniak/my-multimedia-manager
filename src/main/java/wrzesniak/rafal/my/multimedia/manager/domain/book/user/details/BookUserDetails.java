package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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

    public BookUserDetails(BookUserId id) {
        this.id = id;
    }

}
