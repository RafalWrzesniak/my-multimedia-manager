package wrzesniak.rafal.my.multimedia.manager.domain.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(ISBN isbn);

    Optional<Book> findByLubimyCzytacUrl(URL lubimyCzytacUrl);

    List<Book> findByAuthorId(long authorId);

    List<Book> findByPublisher(String publisher);

    @Query(value = "select * from multimedia.book b " +
            "join multimedia.book_content_list_content_list bclcl " +
            "on bclcl.content_list_id = b.id " +
            "where bclcl.book_content_list_id = ?1",
            nativeQuery = true)
    List<Book> findBooksInContentList(Long contentListId, Pageable pageRequest);
}
