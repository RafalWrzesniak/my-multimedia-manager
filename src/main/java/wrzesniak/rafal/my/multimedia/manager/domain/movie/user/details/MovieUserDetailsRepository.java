package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import org.springframework.data.jpa.repository.JpaRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductDetailsRepository;

public interface MovieUserDetailsRepository extends JpaRepository<MovieUserDetails, MovieUserId>, ProductDetailsRepository<MovieUserDetails> {



}
