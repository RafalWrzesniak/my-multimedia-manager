package wrzesniak.rafal.my.multimedia.manager.domain.actor;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb.FilmwebActorUrl;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;

import javax.persistence.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Actor {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ImdbId
    @Column(unique = true)
    private String imdbId;

    private String name;
    private LocalDate birthDate;
    private LocalDate deathDate;

    @FilmwebActorUrl
    @Column(unique = true)
    private URL filmwebUrl;

    @ManyToMany
    @JsonBackReference
    @ToString.Exclude
    private List<Movie> playedInMovies;
    @ManyToMany
    @JsonBackReference
    @ToString.Exclude
    private List<Movie> directedMovies;
    @ManyToMany
    @JsonBackReference
    @ToString.Exclude
    private List<Movie> wroteMovies;

    private LocalDate createdOn;

    public Path getImagePath() {
        return Path.of("images", "actor", imdbId.concat(".jpg"));
    }

    public Integer getAge() {
        if(birthDate == null) return null;
        return Period.between(
                birthDate,
                Optional.ofNullable(deathDate).orElse(LocalDate.now())
        ).getYears();
    }

    public void addToMovieAs(Movie movie, Role role) {
        if(Role.Actor.equals(role)) {
            addToListIfDoNotExist(playedInMovies, movie);
        } else if (Role.Director.equals(role)) {
            addToListIfDoNotExist(directedMovies, movie);
        } else if(Role.Writer.equals(role)) {
            addToListIfDoNotExist(wroteMovies, movie);
        }
    }

    private void addToListIfDoNotExist(List<Movie> list, Movie movie) {
        if(list == null) {
            list = new ArrayList<>();
        }
        if(!list.contains(movie)) {
            list.add(movie);
        }
    }

}
