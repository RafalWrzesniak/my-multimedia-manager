package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.game.Game;

import javax.persistence.Entity;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.GameList;

@Entity
@NoArgsConstructor
public class GameContentList extends BaseContentList<Game>  {

    public GameContentList(String listName) {
        super(listName, GameList);
    }

}