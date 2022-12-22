package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;

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
public class ActorContentList {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    private List<Actor> actorList;

    public ActorContentList(String listName) {
        this.name = listName;
    }

    public boolean addMovie(Actor actor) {
        if(actorList == null) {
            actorList = new ArrayList<>();
        }
        if(actorList.contains(actor)) {
            return false;
        }
        actorList.add(actor);
        return true;
    }

    public List<Actor> getActors() {
        return new ArrayList<>(actorList);
    }
}
