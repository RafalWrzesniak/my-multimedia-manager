package wrzesniak.rafal.my.multimedia.manager.domain.game.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto implements Serializable {

    private String name;
    private URL url;
    private URL image;
    private String description;
    private String publisher;
    private List<String> genre;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<PlayMode> playMode;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<GamePlatform> gamePlatform;
    private Author author;
    @EqualsAndHashCode.Exclude
    private AggregateRating aggregateRating;
    private LocalDate releaseDate;

    public record Author(String name) {
    }

    public record AggregateRating(BigDecimal ratingValue,
                                   int ratingCount) {
    }

}
