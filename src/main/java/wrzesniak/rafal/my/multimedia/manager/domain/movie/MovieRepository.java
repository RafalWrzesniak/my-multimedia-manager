package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE m.imdbId = ?1")
    Optional<Movie> findByImdbId(String imdbId);

}
