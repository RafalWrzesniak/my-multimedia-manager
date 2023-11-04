package wrzesniak.rafal.my.multimedia.manager.domain.book.user.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductUserDetailsAbstract;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@With
@Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BookUserDetailsDynamo extends ProductUserDetailsAbstract<BookUserDetailsDynamo> {

    private String username;
    private String bookId;
    private LocalDate readOn;
    @Enumerated(EnumType.STRING)
    private BookFormat bookFormat;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public BookUserDetailsDynamo(String username, String bookId) {
        this.username = username;
        this.bookId = bookId;
        this.createdOn = LocalDateTime.now();
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @DynamoDbSortKey
    public String getBookId() {
        return bookId;
    }

    @Override
    public LocalDate getFinishedOn() {
        return readOn;
    }

    @Override
    public BookUserDetailsDynamo withFinishedOn(LocalDate localDate) {
        return this.withReadOn(localDate);
    }

    @Override
    public String getId() {
        return bookId;
    }
}
