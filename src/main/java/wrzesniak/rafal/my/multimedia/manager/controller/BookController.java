package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;

import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("book")
public class BookController extends BaseProductController<BookWithUserDetailsDto, Book, BookUserDetails, BookListWithUserDetails> {

    private final BookFacade bookFacade;

    public BookController(BookFacade bookFacade) {
        super(bookFacade);
        this.bookFacade = bookFacade;
    }

    @PostMapping("/createBookUrl")
    public Book createBookFromUrl(@RequestParam String bookUrl,
                                  @RequestParam(required = false) BookFormat bookFormat,
                                  @RequestParam(required = false) String listName) {
        Book book = bookFacade.createFromUrl(toURL(bookUrl));
        bookFacade.setFormatForUserBook(book, bookFormat);
        Optional.ofNullable(listName).ifPresent(list -> bookFacade.addProductToList(book, list));
        return book;
    }

    @PostMapping("/createBookDto")
    public Book createBookFromDto(@RequestBody BookDto bookDto,
                                  @RequestParam(required = false) String listName) {
        Book book = bookFacade.createBookFromDto(bookDto);
        Optional.ofNullable(listName).ifPresent(list -> bookFacade.addProductToList(book, list));
        return book;
    }

    @Override
    @ApiIgnore
    public Book createProductFromUrl(@RequestParam String url,
                                     @RequestParam(required = false) String listName) {
        throw new IllegalStateException("This endpoint is not accessible for this controller. Please try /book/createBookUrl");
    }

    @GetMapping("/findByAuthorId")
    public List<BookWithUserDetailsDto> getBooksByAuthorId(@RequestParam long authorId) {
        return bookFacade.findByAuthorId(authorId);
    }

    @PostMapping("/{bookId}/format")
    public void setBookFormat(@PathVariable long bookId,
                              @RequestParam BookFormat bookFormat) {
        bookFacade.setFormatForUserBook(bookId, bookFormat);
    }

}
