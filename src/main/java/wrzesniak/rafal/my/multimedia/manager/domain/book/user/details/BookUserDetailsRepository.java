package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import org.springframework.data.jpa.repository.JpaRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductDetailsRepository;

public interface BookUserDetailsRepository extends JpaRepository<BookUserDetails, BookUserId>, ProductDetailsRepository<BookUserDetails> {


}
