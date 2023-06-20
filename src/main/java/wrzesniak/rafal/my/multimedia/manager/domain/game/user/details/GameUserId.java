package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class GameUserId implements Serializable, ProductUserId {

    private long gameId;
    private long userId;

    public static GameUserId of(Game game, User user) {
        return new GameUserId(game.getId(), user.getId());
    }

    @Override
    public long getProductId() {
        return gameId;
    }
}
