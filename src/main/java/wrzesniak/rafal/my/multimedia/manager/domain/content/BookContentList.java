package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;

import javax.persistence.Entity;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.BOOK_LIST;

@Entity
@NoArgsConstructor
public class BookContentList extends BaseContentList<Book> {

    public BookContentList(String listName) {
        super(listName, BOOK_LIST);
    }

}
