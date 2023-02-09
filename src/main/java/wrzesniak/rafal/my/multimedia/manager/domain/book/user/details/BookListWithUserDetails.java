package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class BookListWithUserDetails {

    Long id;
    String name;
    List<BookWithUserDetailsDto> bookWithUserDetailsDtos;

}
