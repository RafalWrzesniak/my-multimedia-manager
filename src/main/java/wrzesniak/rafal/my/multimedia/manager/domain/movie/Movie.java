package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Role;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebSearchable;

import javax.persistence.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;


@With
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Movie implements FilmwebSearchable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String imdbId;

    private String title;
    private String polishTitle;
    private LocalDate releaseDate;
    private Integer runtimeMins;
    private double imDbRating;
    private Integer imDbRatingVotes;

    private URL filmwebUrl;

    @ManyToMany
    @ToString.Exclude
    @JsonManagedReference
    private List<Actor> actorList;
    @ManyToMany
    @ToString.Exclude
    @JsonManagedReference
    private List<Actor> directorList;
    @ManyToMany
    @ToString.Exclude
    @JsonManagedReference
    private List<Actor> writerList;

    @ElementCollection
    private List<String> genreList = new ArrayList<>();
    @ElementCollection
    private List<String> countryList = new ArrayList<>();

    @ToString.Exclude
    private String plotLocal;

    public Path getImagePath() {
        return Path.of("images", "movie", imdbId.concat(".jpg"));
    }

    @Override
    public String getFilmwebSearchString() {
        return String.format("%s (%s)", title, releaseDate.getYear());
    }

    public void addRole(Actor actor, Role role) {
        if(Role.Actor.equals(role)) {
            addToListIfDoNotExist(actorList, actor);
        } else if (Role.Director.equals(role)) {
            addToListIfDoNotExist(directorList, actor);
        } else if(Role.Writer.equals(role)) {
            addToListIfDoNotExist(writerList, actor);
        } else {
            return;
        }
        actor.addToMovieAs(this, role);
    }

    public void addActorsWithRole(List<Actor> actorsToAdd, Role role) {
        actorsToAdd.forEach(actor -> addRole(actor, role));
    }

    private void addToListIfDoNotExist(List<Actor> list, Actor actor) {
        if(list == null) {
            list = new ArrayList<>();
        }
        if(!list.contains(actor)) {
            list.add(actor);
        }
    }

}
