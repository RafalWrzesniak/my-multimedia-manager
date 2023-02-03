package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotFoundException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchNameException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoSuchUserException;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.BookList;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Validated
@RestController
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final UserController userController;
    private final UserService userService;
    private final AuthorRepository authorRepository;

    @PostMapping("/{bookUrl}/{listName}")
    public Book createBookFromUrl(String bookUrl, @RequestParam(required = false) String listName) {
        Book book = bookService.createBookFromUrl(toURL(bookUrl));
        addBookToListIfExist(book, listName);
        return book;
    }

    @PostMapping("/create")
    public Book createBookFromDto(@RequestBody BookDto bookDto, @RequestParam(required = false) String listName) {
        Book book = bookService.createBookFromDto(bookDto);
        addBookToListIfExist(book, listName);
        return book;
    }

    @GetMapping("/findById/{id}")
    public Optional<Book> getBookById(long bookId) {
        return bookRepository.findById(bookId);
    }

    @GetMapping("/findByIsbn/{isbn}")
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(ISBN.of(isbn));
    }

    @GetMapping("/findByAuthor/{authorId}")
    public List<Book> getBookByAuthorId(long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @GetMapping("/authors")
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @GetMapping("/")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/markAsRead/{bookId}/{date}")
    public Book markBookAsRead(long bookId,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        return bookService.markBookAsRead(book, date != null ? date : LocalDate.now());
    }

    @GetMapping("/list/{listName}")
    public BookContentList getBookContentListByName(@RequestParam String listName) {
        return (BookContentList) userController.getCurrentUser().getContentListByName(listName, BookList).orElseThrow(NoListWithSuchNameException::new);
    }

    @PostMapping("/list/{listName}")
    public BookContentList addBookContentListToUser(@RequestParam String listName) {
        User user = userController.getCurrentUser();
        return userService.addNewContentListToUser(user, listName, BookList);
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

    @PostMapping("/list/{currentListName}/{newListName}/{bookId}")
    public void moveBookToAnotherList(@RequestParam String currentListName, @RequestParam String newListName, @RequestParam long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        userService.removeObjectFromContentList(userController.getCurrentUser(), currentListName, BookList, book);
        userService.addObjectToContentList(userController.getCurrentUser(), newListName, BookList, book);
    }


    private void addBookToListIfExist(Book book, String listName) {
        try {
            userService.addObjectToContentList(userController.getCurrentUser(), listName, BookList, book);
        } catch (NoListWithSuchNameException e) {
            log.warn("Could not add movie `{}` to list `{}`, because list does not exist!", book.getTitle(), listName);
        }
        catch(NoSuchUserException noSuchUserException) {
            log.warn("Could not add movie `{}` to list `{}`, because user is unknown!", book.getTitle(), listName);
        }
    }
}
