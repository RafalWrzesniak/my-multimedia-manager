package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.book.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFormat;

import java.net.URL;
import java.time.LocalDate;

@Value
@Builder
public class BookWithUserDetailsDto {

    Long id;
    String title;
    String category;
    String description;
    String publisher;
    int numberOfPages;
    String isbn;
    Author author;
    URL lubimyCzytacUrl;
    BookFormat bookFormat;
    LocalDate datePublished;
    LocalDate createdOn;
    LocalDate readOn;

    public static BookWithUserDetailsDto of(Book book, BookUserDetails details) {
        return BookWithUserDetailsDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .category(book.getCategory())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .numberOfPages(book.getNumberOfPages())
                .isbn(book.getIsbn().getValue())
                .author(book.getAuthor())
                .lubimyCzytacUrl(book.getLubimyCzytacUrl())
                .bookFormat(details.getBookFormat())
                .datePublished(book.getDatePublished())
                .createdOn(book.getCreatedOn())
                .readOn(details.getReadOn())
                .build();
    }
}
