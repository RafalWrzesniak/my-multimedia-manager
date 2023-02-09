package wrzesniak.rafal.my.multimedia.manager.domain.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac.LubimyCzytacService;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final WebOperations webOperations;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LubimyCzytacService lubimyCzytacService;
    private final BookUserDetailsRepository bookUserDetailsRepository;

    public Book createBookFromDto(BookDto bookDto) {
        Optional<Book> bookByUrlOrIsbn = findBookByUrlOrIsbn(bookDto.getUrl(), ISBN.of(bookDto.getIsbn()));
        if(bookByUrlOrIsbn.isPresent()) {
            log.info("Book already exist with dto: {}", bookDto);
            return bookByUrlOrIsbn.get();
        }
        Book book = DtoMapper.mapToBook(bookDto);
        addOrCreateAuthorToBook(book, bookDto.getAuthor());
        Book savedBook = bookRepository.save(book);
        webOperations.downloadImageToDirectory(toURL(bookDto.getImage()), savedBook.getImagePath());
        log.info("Book created from URL: {}", book);
        return savedBook;
    }

    public Book createBookFromUrl(URL lubimyCzytacBookUrl, BookFormat bookFormat) {
        return lubimyCzytacService.createBookDtoFromUrl(lubimyCzytacBookUrl)
                .map(bookDto -> bookDto.withBookFormat(bookFormat))
                .map(this::createBookFromDto)
                .orElseThrow(BookNotCreatedException::new);
    }

    public void markBookAsRead(Book book, User user, LocalDate finishReadingDay) {
        BookUserId bookUserId = BookUserId.of(book, user);
        Optional<BookUserDetails> repoDetails = bookUserDetailsRepository.findById(bookUserId);
        BookUserDetails bookDetails = repoDetails.orElse(new BookUserDetails(bookUserId));
        bookDetails.setReadOn(firstNonNull(finishReadingDay, LocalDate.now()));
        log.info("Marking book `{}` as read on {}", book.getTitle(), bookDetails.getReadOn());
        bookUserDetailsRepository.save(bookDetails);
    }

    private void addOrCreateAuthorToBook(Book book, AuthorDto authorDto) {
        authorRepository.findByName(authorDto.getName())
                .ifPresentOrElse(book::setAuthor, () -> {
                    Author author = DtoMapper.mapToAuthor(authorDto);
                    book.setAuthor(author);
                });
    }

    public Optional<Book> findBookByUrlOrIsbn(String bookUrl, ISBN isbn) {
        Optional<Book> existingBookByUrl = bookRepository.findByLubimyCzytacUrl(toURL(bookUrl));
        Optional<Book> existingBookByIsbn = bookRepository.findByIsbn(isbn);
        return existingBookByUrl.isPresent() ? existingBookByUrl : existingBookByIsbn;
    }

}
