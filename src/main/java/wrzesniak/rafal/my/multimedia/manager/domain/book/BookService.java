package wrzesniak.rafal.my.multimedia.manager.domain.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac.LubimyCzytacService;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LubimyCzytacService lubimyCzytacService;
    private final WebOperations webOperations;

    public Book createBookFromUrl(URL lubimyCzytacBookUrl) {
        Optional<BookDto> bookDtoFromUrl = lubimyCzytacService.createBookDtoFromUrl(lubimyCzytacBookUrl);
        BookDto bookDto = bookDtoFromUrl.orElseThrow(BookNotCreatedException::new);
        Optional<Book> bookByIsbn = bookRepository.findByIsbn(bookDto.getIsbn());
        if(bookByIsbn.isPresent()) {
            return bookByIsbn.get();
        }
        Book book = DtoMapper.mapToBook(bookDto);
        addOrCreateAuthorToBook(book, bookDto.getAuthor());
        webOperations.downloadImageToDirectory(bookDto.getImage(), book.getImagePath());
        Book savedBook = bookRepository.save(book);
        log.info("Book created from URL: {}", book);
        return savedBook;
    }

    public Book markBookAsRead(Book book) {
        return markBookAsRead(book, LocalDate.now());
    }

    public Book markBookAsRead(Book book, LocalDate finishReadingDay) {
        log.info("Marking book `{}` as read on {}", book.getTitle(), finishReadingDay);
        book.setReadOn(finishReadingDay);
        return bookRepository.save(book);
    }

    private void addOrCreateAuthorToBook(Book book, AuthorDto authorDto) {
        authorRepository.findByName(authorDto.getName())
                .ifPresentOrElse(book::setAuthor, () -> {
                    Author author = DtoMapper.mapToAuthor(authorDto);
                    book.setAuthor(author);
                });
    }

}
