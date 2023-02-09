package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.book.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BookUserId implements Serializable {

    private long bookId;
    private long userId;

    public static BookUserId of(Book book, User user) {
        return new BookUserId(book.getId(), user.getId());
    }


}