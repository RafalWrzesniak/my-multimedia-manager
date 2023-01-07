package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieContentList implements ContentList {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Pattern(regexp = LIST_NAME_REGEX, message = LIST_NAME_MESSAGE)
    private String name;
    @ManyToMany
    private List<Movie> movies;

    public MovieContentList(String listName) {
        this.name = listName;
        this.movies = new ArrayList<>();
    }

    public boolean addMovie(Movie movie) {
        if (movies.contains(movie)) {
            return false;
        }
        return movies.add(movie);
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movies);
    }

    public void removeMovie(Movie movieToRemove) {
        movies.remove(movieToRemove);
    }
}
