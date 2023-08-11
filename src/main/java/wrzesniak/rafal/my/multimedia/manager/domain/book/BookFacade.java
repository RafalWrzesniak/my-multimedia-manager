package wrzesniak.rafal.my.multimedia.manager.domain.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.DefaultProductService;
import wrzesniak.rafal.my.multimedia.manager.domain.GenericUserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.repository.BookRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.service.BookCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.BOOK_LIST;

@Slf4j
@Service
public class BookFacade extends DefaultProductService<BookWithUserDetailsDto, Book, BookUserDetails, BookListWithUserDetails> {

    private final BookCreatorService bookCreatorService;
    private final BookRepository bookRepository;

    public BookFacade(UserService userService, BookRepository bookRepository, GenericUserObjectDetailsFounder<BookWithUserDetailsDto, Book, BookUserDetails, BookListWithUserDetails> genericUserObjectDetailsFounder, BookCreatorService bookCreatorService) {
        super(userService, bookRepository, genericUserObjectDetailsFounder, BOOK_LIST, BookWithUserDetailsDto::getReadOn, bookCreatorService, BookUserDetails::withReadOn);
        this.bookCreatorService = bookCreatorService;
        this.bookRepository = bookRepository;
    }

    public Book createBookFromDto(BookDto bookDto) {
        Book book = bookCreatorService.createBookFromDto(bookDto);
        addProductToList(book, BOOK_LIST.getAllProductsListName());
        return book;
    }

    public void setFormatForUserBook(long bookId, BookFormat bookFormat) {
        bookRepository.findById(bookId).ifPresent(book -> setFormatForUserBook(book, bookFormat));
    }

    public void setFormatForUserBook(Book book, BookFormat bookFormat) {
        BookUserDetails bookDetails = super.getProductUserDetails(book);
        bookDetails.setBookFormat(bookFormat);
        super.saveUserProductDetails(bookDetails);
    }

    public List<BookWithUserDetailsDto> findByAuthorId(long authorId) {
        return bookRepository.findByAuthorId(authorId).stream()
                .map(super::mapToProductWithUserDetails)
                .toList();
    }
}
