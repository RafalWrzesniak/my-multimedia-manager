package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieContentList {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
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
}
