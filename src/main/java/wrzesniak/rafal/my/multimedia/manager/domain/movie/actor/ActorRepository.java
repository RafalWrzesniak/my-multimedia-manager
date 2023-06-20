package wrzesniak.rafal.my.multimedia.manager.domain.movie.actor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    @Query("SELECT a FROM Actor a WHERE a.imdbId = ?1")
    Optional<Actor> findByImdbId(String imdbId);

}
