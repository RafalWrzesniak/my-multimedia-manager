package wrzesniak.rafal.my.multimedia.manager.domain.content;

import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;

import javax.persistence.Entity;

@Entity
public class ActorContentList extends BaseContentList<Actor> {

    public ActorContentList(String listName) {
        super(listName);
    }

}
