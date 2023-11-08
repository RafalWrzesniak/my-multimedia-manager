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

import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("book")
public class BookController extends BaseProductController<BookWithUserDetailsDto, BookUserDetailsDynamo, BookListWithUserDetails, BookDynamo> {

    private final BookFacade bookFacade;

    public BookController(BookFacade bookFacade) {
        super(bookFacade);
        this.bookFacade = bookFacade;
    }

    @PostMapping("/createBookUrl")
    public BookWithUserDetailsDto createBookFromUrl(@RequestParam String url,
                                  @RequestParam(required = false) BookFormat bookFormat,
                                  @RequestParam(required = false) String listId) {
        BookWithUserDetailsDto book = bookFacade.createFromUrl(toURL(url));
        bookFacade.setFormatForUserBook(book.getLubimyCzytacUrl().toString(), bookFormat);
        Optional.ofNullable(listId).ifPresent(list -> bookFacade.addProductToList(url, list));
        return book;
    }


    @Override
    public BookWithUserDetailsDto createProductFromUrl(@RequestParam String url,
                                     @RequestParam(required = false) String listId) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /book/createBookUrl");
    }

    @PostMapping("/format")
    public void setBookFormat(@RequestParam String bookId,
                              @RequestParam BookFormat bookFormat) {
        bookFacade.setFormatForUserBook(bookId, bookFormat);
    }

}
