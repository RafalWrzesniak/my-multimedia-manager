package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Series;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class BookWithUserDetailsDto {

    String id;
    String title;
    String category;
    String description;
    String publisher;
    int numberOfPages;
    String isbn;
    String author;
    URL lubimyCzytacUrl;
    BookFormat bookFormat;
    LocalDate datePublished;
    LocalDateTime createdOn;
    LocalDateTime updatedOn;
    Series series;
    LocalDate readOn;
    String webImageUrl;


    public static BookWithUserDetailsDto of(BookDynamo bookDynamo, BookUserDetailsDynamo bookUserDetailsDynamo) {
        return BookWithUserDetailsDto.builder()
                .id(bookDynamo.getId())
                .title(bookDynamo.getTitle())
                .category(bookDynamo.getCategory())
                .description(bookDynamo.getDescription())
                .publisher(bookDynamo.getPublisher())
                .numberOfPages(bookDynamo.getNumberOfPages())
                .isbn(bookDynamo.getIsbn().getValue())
                .author(bookDynamo.getAuthor())
                .lubimyCzytacUrl(bookDynamo.getLubimyCzytacUrl())
                .bookFormat(bookUserDetailsDynamo.getBookFormat())
                .datePublished(bookDynamo.getDatePublished())
                .createdOn(bookUserDetailsDynamo.getCreatedOn())
                .updatedOn(bookUserDetailsDynamo.getUpdatedOn())
                .readOn(bookUserDetailsDynamo.getReadOn())
                .series(bookDynamo.getSeries())
                .webImageUrl(bookDynamo.getWebImageUrl())
                .build();
    }

    public static BookWithUserDetailsDto of(Book book, BookUserDetails details) {
        return BookWithUserDetailsDto.builder()
                .id(book.getId().toString())
                .title(book.getTitle())
                .category(book.getCategory())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .numberOfPages(book.getNumberOfPages())
                .isbn(book.getIsbn().getValue())
                .author(book.getAuthor().toString())
                .lubimyCzytacUrl(book.getLubimyCzytacUrl())
                .bookFormat(details.getBookFormat())
                .datePublished(book.getDatePublished())
                .createdOn(book.getCreatedOn().atStartOfDay())
                .readOn(details.getReadOn())
                .series(book.getSeries())
                .webImageUrl("/" + book.getImagePath().get(0).subpath(3, 6).toString().replaceAll("\\\\", "/"))
                .build();
    }
}
