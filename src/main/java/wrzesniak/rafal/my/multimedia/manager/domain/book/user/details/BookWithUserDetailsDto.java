package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Series;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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

    public static BookWithUserDetailsDto fromSimpleItemAndUserDetails(SimpleItem simpleItem, BookUserDetailsDynamo bookUserDetailsDynamo) {
        return BookWithUserDetailsDto.builder()
                .id(simpleItem.getId())
                .title(simpleItem.getDisplayedTitle())
                .webImageUrl(simpleItem.getWebImageUrl())
                .readOn(Optional.ofNullable(bookUserDetailsDynamo).map(BookUserDetailsDynamo::getReadOn).orElse(null))
                .bookFormat(Optional.ofNullable(bookUserDetailsDynamo).map(BookUserDetailsDynamo::getBookFormat).orElse(null))
                .createdOn(Optional.ofNullable(bookUserDetailsDynamo).map(BookUserDetailsDynamo::getCreatedOn).orElse(null))
                .updatedOn(Optional.ofNullable(bookUserDetailsDynamo).map(BookUserDetailsDynamo::getUpdatedOn).orElse(null))
                .build();
    }

}
