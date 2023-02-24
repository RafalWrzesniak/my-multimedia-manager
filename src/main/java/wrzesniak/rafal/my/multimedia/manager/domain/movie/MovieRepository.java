package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByImdbId(String imdbId);

    Optional<Movie> findByFilmwebUrl(URL filmwebMovieUrl);

    List<Movie> findByPolishTitleContainingIgnoreCaseOrTitleContainingIgnoreCase(String polishTitle, String title);

    @Query("Select m From Movie m")
    List<Movie> findAllMovies(Pageable page);

}
