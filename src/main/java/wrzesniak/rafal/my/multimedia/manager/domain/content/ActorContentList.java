package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;

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
public class ActorContentList implements ContentList {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Pattern(regexp = LIST_NAME_REGEX, message = LIST_NAME_MESSAGE)
    private String name;
    @ManyToMany
    private List<Actor> actorList;

    public ActorContentList(String listName) {
        this.name = listName;
        this.actorList = new ArrayList<>();
    }

    public boolean addActor(Actor actor) {
        if(actorList.contains(actor)) {
            return false;
        }
        return actorList.add(actor);
    }

    public List<Actor> getActors() {
        return new ArrayList<>(actorList);
    }

    public void removeActor(Actor actorToRemove) {
        actorList.remove(actorToRemove);
    }
}
