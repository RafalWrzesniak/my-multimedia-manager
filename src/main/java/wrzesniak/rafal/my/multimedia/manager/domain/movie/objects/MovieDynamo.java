package wrzesniak.rafal.my.multimedia.manager.domain.movie.objects;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@With
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovieDynamo implements Product {

    @EqualsAndHashCode.Include
    private String title;
    private String polishTitle;
    @EqualsAndHashCode.Include
    private URL filmwebUrl;
    private LocalDate releaseDate;
    private LocalDateTime createdOn;
    private Integer runtimeMins;
    private BigDecimal rating;
    private Integer ratingVotes;
    private String webImageUrl;
    private String plotLocal;

    private Set<String> genreList = new HashSet<>();
    private Set<String> countryList = new HashSet<>();

    @DynamoDbPartitionKey
    public URL getFilmwebUrl() {
        return filmwebUrl;
    }

    public String getMovieId() {
        return filmwebUrl.toString();
    }

    @Override
    public String getId() {
        return getFilmwebUrl().toString();
    }
}
