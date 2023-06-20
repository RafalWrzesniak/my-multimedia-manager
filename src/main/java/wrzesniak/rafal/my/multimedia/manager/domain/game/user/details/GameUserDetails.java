package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;

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

    public GameUserDetails(ProductUserId productUserId) {
        this.id = new GameUserId(productUserId.getProductId(), productUserId.getUserId());
    }

}
