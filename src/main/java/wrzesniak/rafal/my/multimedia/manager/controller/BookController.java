package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.*;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.BookList;
import static wrzesniak.rafal.my.multimedia.manager.domain.user.RegistrationService.ALL_BOOKS;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@RestController
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {

    private static final int PAGE_SIZE = 20;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final UserController userController;
    private final UserService userService;
    private final AuthorRepository authorRepository;
    private final UserObjectDetailsFounder detailsFounder;

    @PostMapping("/{bookUrl}/{listName}")
    public Book createBookFromUrl(String bookUrl,
                                  @RequestParam(required = false) BookFormat bookFormat,
                                  @RequestParam(required = false) String listName) {
        Book book = bookService.createBookFromUrl(toURL(bookUrl));
        bookService.setFormatForUserBook(userController.getCurrentUser(), book, bookFormat);
        userService.addObjectToListIfExists(userController.getCurrentUser(), ALL_BOOKS, BookList, book);
        userService.addObjectToListIfExists(userController.getCurrentUser(), listName, BookList, book);
        return book;
    }

    @PostMapping("/create")
    public Book createBookFromDto(@RequestBody BookDto bookDto, @RequestParam(required = false) String listName) {
        Book book = bookService.createBookFromDto(bookDto);
        userService.addObjectToListIfExists(userController.getCurrentUser(), ALL_BOOKS, BookList, book);
        userService.addObjectToListIfExists(userController.getCurrentUser(), listName, BookList, book);
        return book;
    }

    @GetMapping("/findById/{id}")
    public Optional<BookWithUserDetailsDto> getBookById(long bookId) {
        return bookRepository.findById(bookId)
                .map(book -> detailsFounder.findDetailedBookDataFor(book, userController.getCurrentUser()));
    }

    @GetMapping("/findByIsbn/{isbn}")
    public Optional<BookWithUserDetailsDto> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(ISBN.of(isbn))
                .map(book -> detailsFounder.findDetailedBookDataFor(book, userController.getCurrentUser()));
    }

    @GetMapping("/findByAuthor/{authorId}")
    public List<BookWithUserDetailsDto> getBookByAuthorId(long authorId) {
        return bookRepository.findByAuthorId(authorId).stream()
                .map(book -> detailsFounder.findDetailedBookDataFor(book, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/find/title")
    public List<BookWithUserDetailsDto> findBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(book -> detailsFounder.findDetailedBookDataFor(book, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/authors")
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @GetMapping("/publisher/{publisherName}")
    public List<BookWithUserDetailsDto> getAllBookFromPublisher(@PathVariable String publisherName) {
        return bookRepository.findByPublisher(publisherName).stream()
                .map(book -> detailsFounder.findDetailedBookDataFor(book, userController.getCurrentUser()))
                .toList();
    }

    @GetMapping("/")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/markAsRead/{bookId}/{date}")
    public void markBookAsRead(long bookId,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        bookService.markBookAsRead(book, userController.getCurrentUser(), date);
    }

    @DeleteMapping("/book/delete")
    public void removeBookFromDatabase(long bookId) {
        bookRepository.deleteById(bookId);
        log.info("Book with id {} deleted from database", bookId);
    }

    @GetMapping("/list/{listName}")
    public BookListWithUserDetails getBookContentListByName(@RequestParam String listName,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                            @RequestParam(defaultValue = "id") String sortKey,
                                                            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        return userController.getCurrentUser()
                .getContentListByName(listName, BookList)
                .map(baseContentList -> detailsFounder.findDetailedBookDataFor((BookContentList) baseContentList, userController.getCurrentUser(), PageRequest.of(page, PAGE_SIZE, Sort.by(direction, sortKey))))
                .orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/list/{listName}")
    public BookListWithUserDetails addBookContentListToUser(@RequestParam String listName) {
        User user = userController.getCurrentUser();
        BookContentList bookContentList = userService.addNewContentListToUser(user, listName, BookList);
        return detailsFounder.findDetailedBookDataFor(bookContentList, user, PageRequest.ofSize(PAGE_SIZE));
    }

    @DeleteMapping("/list/{listName}")
    public void removeBookList(@RequestParam String listName) {
        userService.removeContentListFromUser(userController.getCurrentUser(), listName, BookList);
    }

    @PostMapping("/list/{listName}/{bookId}")
    public void addBookToUserContentList(@RequestParam String listName, @RequestParam long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        userService.addObjectToContentList(userController.getCurrentUser(), listName, BookList, book);
    }

    @DeleteMapping("/list/{listName}/{bookId}")
    public void removeBookFromList(@RequestParam String listName, @RequestParam long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        userService.removeObjectFromContentList(userController.getCurrentUser(), listName, BookList, book);
    }


    @PostMapping("/list/{currentListName}/{newListName}")
    public void moveBookToAnotherList(@RequestParam List<Long> bookIds, @RequestParam String currentListName, @RequestParam String newListName, boolean removeFromOriginal) {
        bookIds.stream()
                .map(bookId -> bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new))
                .forEach(book -> userService.moveObjectFromListToList(userController.getCurrentUser(), book, BookList, currentListName, newListName, removeFromOriginal));
    }
}
