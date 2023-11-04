package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductUserDetailsAbstract;

import java.time.LocalDate;
import java.time.LocalDateTime;

@With
@Data
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MovieUserDetailsDynamo extends ProductUserDetailsAbstract<MovieUserDetailsDynamo> {

    private String username;
    private String movieId;
    private LocalDate watchedOn;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public MovieUserDetailsDynamo(String username, String movieId) {
        this.username = username;
        this.movieId = movieId;
        this.createdOn = LocalDateTime.now();
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @DynamoDbSortKey
    public String getMovieId() {
        return movieId;
    }

    @Override
    public String getId() {
        return getMovieId();
    }

    @Override
    public LocalDate getFinishedOn() {
        return getWatchedOn();
    }

    @Override
    public MovieUserDetailsDynamo withFinishedOn(LocalDate localDate) {
        return this.withWatchedOn(localDate);
    }
}
