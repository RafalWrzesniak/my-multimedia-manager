package wrzesniak.rafal.my.multimedia.manager.domain.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(ISBN isbn);

    Optional<Book> findByLubimyCzytacUrl(URL lubimyCzytacUrl);

    List<Book> findByAuthorId(long authorId);

    List<Book> findByPublisher(String publisher);
}
