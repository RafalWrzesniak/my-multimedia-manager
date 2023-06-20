package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDate;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieUserDetails {

    @EmbeddedId
    private MovieUserId id;

    private LocalDate watchedOn;

    public MovieUserDetails(ProductUserId movieUserId) {
        this.id = new MovieUserId(movieUserId.getProductId(), movieUserId.getUserId());
    }

}
