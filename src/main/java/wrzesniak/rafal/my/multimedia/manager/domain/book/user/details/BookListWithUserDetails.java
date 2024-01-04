package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.With;
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
    int productsNumber;
    boolean isAllBooksList;
    ContentListType listType;
    List<BookWithUserDetailsDto> bookWithUserDetailsDtos;

    public static BookListWithUserDetails of(ContentListDynamo contentListDynamo, List<BookWithUserDetailsDto> bookWithUserDetailsDtos, int productsNumber) {
        return BookListWithUserDetails.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .isAllBooksList(contentListDynamo.isAllContentList())
                .productsNumber(productsNumber)
                .listType(BOOK_LIST)
                .bookWithUserDetailsDtos(bookWithUserDetailsDtos)
                .build();
    }
}
