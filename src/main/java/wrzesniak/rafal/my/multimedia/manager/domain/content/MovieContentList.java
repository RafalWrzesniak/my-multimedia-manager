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
    private List<Movie> movieList;

    public MovieContentList(String listName) {
        this.name = listName;
    }

    public boolean addMovie(Movie movie) {
        if(movieList == null) {
            movieList = new ArrayList<>();
        }
        if(movieList.contains(movie)) {
            return false;
        }
        movieList.add(movie);
        return true;
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movieList);
    }
}
