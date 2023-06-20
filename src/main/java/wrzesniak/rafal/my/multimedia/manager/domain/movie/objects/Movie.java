package wrzesniak.rafal.my.multimedia.manager.domain.movie.objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.Imagable;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Role;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb.FilmwebMovieUrl;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbObject;

import javax.persistence.*;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;


@With
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie implements ImdbObject, Imagable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ImdbId
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String imdbId;

    private String title;
    private String polishTitle;
    private LocalDate releaseDate;
    private Integer runtimeMins;
    private BigDecimal imDbRating;
    private Integer imDbRatingVotes;

    @FilmwebMovieUrl
    private URL filmwebUrl;

    @ManyToMany(cascade = CascadeType.DETACH)
    @ToString.Exclude
    @JsonManagedReference
    private Set<Actor> actorList;
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Set<Actor> directorList;
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Set<Actor> writerList;

    @ElementCollection
    private Set<String> genreList = new HashSet<>();
    @ElementCollection
    private Set<String> countryList = new HashSet<>();

    @ToString.Exclude
    private String plotLocal;

    private LocalDate createdOn;

    @Override
    public String getUniqueId() {
        return imdbId;
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

    private void addToListIfDoNotExist(Set<Actor> set, Actor actor) {
        if(set == null) {
            set = new HashSet<>();
        }
        set.add(actor);
    }

}
