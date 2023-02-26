package wrzesniak.rafal.my.multimedia.manager.domain.game;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByGryOnlineUrl(URL gryOnlineUrl);

    List<Game> findByStudio(String studio, Pageable pageRequest);

    List<Game> findByPublisher(String publisher, Pageable pageRequest);

    List<Game> findByGamePlatform(GamePlatform gamePlatform, Pageable pageRequest);

    List<Game> findByPlayModes(PlayMode playMode, Pageable pageRequest);

    @Query("Select g From Game g")
    List<Game> findAllGames(Pageable pageRequest);

    @Query(value = "select * from multimedia.game g " +
            "join multimedia.game_content_list_content_list gclcl " +
            "on gclcl.content_list_id = g.id " +
            "where gclcl.game_content_list_id = ?1",
            nativeQuery = true)
    List<Game> findGamesInContentList(Long contentListId, Pageable pageRequest);
}
