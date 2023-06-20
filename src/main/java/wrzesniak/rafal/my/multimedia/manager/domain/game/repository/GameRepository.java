package wrzesniak.rafal.my.multimedia.manager.domain.game.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long>, ProductRepository<Game>, JpaSpecificationExecutor<Game> {

    Optional<Game> findByGryOnlineUrl(URL gryOnlineUrl);

    @Query(value = "select * from multimedia.game g " +
            "join multimedia.game_content_list_content_list gclcl " +
            "on gclcl.content_list_id = g.id " +
            "where gclcl.game_content_list_id = ?1",
            nativeQuery = true)
    List<Game> findGamesInContentList(Long contentListId, Pageable pageRequest);

    @Override
    default List<Game> findProductsInContentList(long contentListId, Pageable pageRequest) {
        return findGamesInContentList(contentListId, pageRequest);
    }

}
