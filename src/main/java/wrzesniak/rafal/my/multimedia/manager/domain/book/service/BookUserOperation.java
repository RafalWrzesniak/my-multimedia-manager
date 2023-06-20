package wrzesniak.rafal.my.multimedia.manager.domain.book.service;

import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserOperations;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.util.List;

@Component
public class BookUserOperation implements ProductUserOperations<BookWithUserDetailsDto, Book, BookUserDetails, BookListWithUserDetails> {

    @Override
    public BookWithUserDetailsDto mergeProductWithUserDetails(Book book, BookUserDetails bookUserDetails) {
        return BookWithUserDetailsDto.of(book, bookUserDetails);
    }

    @Override
    public BookListWithUserDetails createDetailedListFrom(BaseContentList<Book> bookList) {
        return BookListWithUserDetails.of((BookContentList) bookList);
    }

    @Override
    public BookListWithUserDetails addDetailedProductsToDetailedList(BookListWithUserDetails list, List<BookWithUserDetailsDto> books) {
        return list.withBookWithUserDetailsDtos(books);
    }

    @Override
    public ProductUserId getProductUserIdFrom(Book book, User user) {
        return BookUserId.of(book, user);
    }

    @Override
    public BookUserDetails createNewProductUserDetails(ProductUserId productUserId) {
        return new BookUserDetails(productUserId);
    }
}
