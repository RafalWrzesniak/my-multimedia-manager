package wrzesniak.rafal.my.multimedia.manager.domain.actor;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.Imagable;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbObject;

import javax.persistence.*;
import java.net.URL;
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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Actor implements ImdbObject, Imagable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ImdbId
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String imdbId;

    private String name;
    private LocalDate birthDate;
    private LocalDate deathDate;

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

    @Override
    public String getUniqueId() {
        return imdbId;
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
