package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import org.springframework.data.jpa.repository.JpaRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductDetailsRepository;

public interface GameUserDetailsRepository extends JpaRepository<GameUserDetails, GameUserId>, ProductDetailsRepository<GameUserDetails> {
}
