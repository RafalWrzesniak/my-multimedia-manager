package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;

import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.BOOK_LIST;

@With
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookListWithUserDetails {

    String id;
    String name;
    int booksNumber;
    boolean isAllBooksList;
    ContentListType listType;
    List<BookWithUserDetailsDto> bookWithUserDetailsDtos;

    public static BookListWithUserDetails of(BookContentList bookContentList) {
        return BookListWithUserDetails.builder()
                .id(bookContentList.getId().toString())
                .name(bookContentList.getName())
                .isAllBooksList(bookContentList.isAllContentList())
                .booksNumber(bookContentList.getContentList().size())
                .listType(BOOK_LIST)
                .build();
    }

    public static BookListWithUserDetails of(ContentListDynamo contentListDynamo, List<BookWithUserDetailsDto> bookWithUserDetailsDtos) {
        return BookListWithUserDetails.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .isAllBooksList(contentListDynamo.isAllContentList())
                .booksNumber(bookWithUserDetailsDtos.size())
                .listType(BOOK_LIST)
                .bookWithUserDetailsDtos(bookWithUserDetailsDtos)
                .build();
    }
}
