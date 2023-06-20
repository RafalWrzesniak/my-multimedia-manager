package wrzesniak.rafal.my.multimedia.manager.domain.book.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.ISBN;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, ProductRepository<Book>, JpaSpecificationExecutor<Book> {

    Optional<Book> findByLubimyCzytacUrl(URL lubimyCzytacUrl);

    @Query(value = "select * from multimedia.book b " +
            "join multimedia.book_content_list_content_list bclcl " +
            "on bclcl.content_list_id = b.id " +
            "where bclcl.book_content_list_id = ?1",
            nativeQuery = true)
    List<Book> findBooksInContentList(Long contentListId, Pageable pageRequest);

    @Override
    default List<Book> findProductsInContentList(long contentListId, Pageable pageRequest) {
        return findBooksInContentList(contentListId, pageRequest);
    }

    List<Book> findByAuthorId(long authorId);

    Optional<Book> findByIsbn(ISBN isbn);

}
