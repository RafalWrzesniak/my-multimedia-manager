package wrzesniak.rafal.my.multimedia.manager.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.AuthorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.AuthorRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.ISBN;
import wrzesniak.rafal.my.multimedia.manager.domain.book.repository.BookRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac.LubimyCzytacService;

import java.net.URL;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCreatorService implements ProductCreatorService<Book> {

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

    @Override
    public Book createProductFromUrl(URL lubimyCzytacBookUrl) {
        return lubimyCzytacService.createBookDtoFromUrl(lubimyCzytacBookUrl)
                .map(this::createBookFromDto)
                .orElseThrow(BookNotCreatedException::new);
    }

    private Optional<Book> findBookByUrlOrIsbn(String bookUrl, ISBN isbn) {
        Optional<Book> existingBookByUrl = bookRepository.findByLubimyCzytacUrl(toURL(bookUrl));
        Optional<Book> existingBookByIsbn = bookRepository.findByIsbn(isbn);
        return existingBookByUrl.isPresent() ? existingBookByUrl : existingBookByIsbn;
    }

    private void addOrCreateAuthorToBook(Book book, AuthorDto authorDto) {
        authorRepository.findByName(authorDto.getName())
                .ifPresentOrElse(book::setAuthor, () -> {
                    Author author = DtoMapper.mapToAuthor(authorDto);
                    book.setAuthor(author);
                });
    }

}
