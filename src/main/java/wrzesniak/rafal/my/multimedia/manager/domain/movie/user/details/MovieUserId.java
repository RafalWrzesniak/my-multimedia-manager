package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MovieUserId implements Serializable, ProductUserId {

    private long movieId;
    private long userId;

    public static MovieUserId of(Movie movie, User user) {
        return new MovieUserId(movie.getId(), user.getId());
    }

    @Override
    public long getProductId() {
        return movieId;
    }
}
