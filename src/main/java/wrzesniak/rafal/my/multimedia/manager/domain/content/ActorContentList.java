package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;

import javax.persistence.Entity;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.ActorList;

@Entity
@NoArgsConstructor
public class ActorContentList extends BaseContentList<Actor> {

    public ActorContentList(String listName) {
        super(listName, ActorList);
    }

}
