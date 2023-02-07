package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

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
public class MovieUserDetails {

    @EmbeddedId
    private MovieUserId id;

    private LocalDate watchedOn;

    public MovieUserDetails(MovieUserId movieUserId) {
        this.id = movieUserId;
    }
}
