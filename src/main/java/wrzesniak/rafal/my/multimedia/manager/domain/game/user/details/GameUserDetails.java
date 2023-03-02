package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GamePlatform;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUserDetails {

    @EmbeddedId
    private GameUserId id;

    private LocalDate finishedOn;

    @Enumerated(EnumType.STRING)
    private GamePlatform gamePlatform;

    private Integer playedHours;

    public GameUserDetails(GameUserId id) {
        this.id = id;
    }

}
