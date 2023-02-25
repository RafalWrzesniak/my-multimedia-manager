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

    @Query(value = "select * from multimedia.movie m " +
            "join multimedia.movie_content_list_content_list mclcl " +
            "on mclcl.content_list_id = m.id " +
            "where mclcl.movie_content_list_id = ?1",
            nativeQuery = true)
    List<Movie> findMoviesInContentList(long contentListId, Pageable pageable);
}
