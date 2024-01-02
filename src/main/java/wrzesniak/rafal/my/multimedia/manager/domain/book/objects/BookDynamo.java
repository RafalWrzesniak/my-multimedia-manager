package wrzesniak.rafal.my.multimedia.manager.domain.book.objects;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;
import wrzesniak.rafal.my.multimedia.manager.util.IsbnDynamoConverter;
import wrzesniak.rafal.my.multimedia.manager.util.SeriesDynamoConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@With
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookDynamo implements Product {

    @EqualsAndHashCode.Include
    private String title;
    private String category;
    @ToString.Exclude
    private String description;
    private String publisher;
    private int numberOfPages;
    private ISBN isbn;
    private String author;
    @EqualsAndHashCode.Include
    private URL lubimyCzytacUrl;
    private Series series;
    private LocalDate datePublished;
    private LocalDateTime createdOn;
    private String webImageUrl;

    @DynamoDbPartitionKey
    public URL getLubimyCzytacUrl() {
        return lubimyCzytacUrl;
    }

    @Override
    public String getId() {
        return lubimyCzytacUrl.toString();
    }

    @DynamoDbConvertedBy(IsbnDynamoConverter.class)
    public ISBN getIsbn() {
        return isbn;
    }

    @DynamoDbConvertedBy(SeriesDynamoConverter.class)
    public Series getSeries() {
        return series;
    }

    public String getDisplayedTitle() {
        return title;
    }
}
