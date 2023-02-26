package wrzesniak.rafal.my.multimedia.manager.domain.game.user.details;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameUserDetailsRepository extends JpaRepository<GameUserDetails, GameUserId> {
}
