package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder;

import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder.TOKEN_HEADER;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("book")
public class BookController extends BaseProductController<BookWithUserDetailsDto, BookUserDetailsDynamo, BookListWithUserDetails, BookDynamo> {

    private final BookFacade bookFacade;
    private final JwtTokenDecoder jwtTokenDecoder;

    public BookController(BookFacade bookFacade, JwtTokenDecoder jwtTokenDecoder) {
        super(bookFacade, jwtTokenDecoder);
        this.bookFacade = bookFacade;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("/createBookUrl")
    public BookWithUserDetailsDto createBookFromUrl(@RequestParam String url,
                                                    @RequestParam(required = false) BookFormat bookFormat,
                                                    @RequestParam(required = false) String listId,
                                                    @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        BookWithUserDetailsDto book = bookFacade.createFromUrl(toURL(url), username);
        bookFacade.setFormatForUserBook(book.getLubimyCzytacUrl().toString(), bookFormat, username);
        Optional.ofNullable(listId).ifPresent(list -> bookFacade.addProductToList(url, list, username));
        return book;
    }


    @Override
    public BookWithUserDetailsDto createProductFromUrl(@RequestParam String url,
                                                       @RequestParam(required = false) String listId,
                                                       @RequestHeader(TOKEN_HEADER) String jwtToken) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /book/createBookUrl");
    }

    @PostMapping("/format")
    public void setBookFormat(@RequestParam String bookId,
                              @RequestParam BookFormat bookFormat,
                              @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        bookFacade.setFormatForUserBook(bookId, bookFormat, username);
    }

}
