package wrzesniak.rafal.my.multimedia.manager.domain.game;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.util.StringCollectionCustomDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto implements Serializable {

    private String name;
    private URL url;
    private URL image;
    private String description;
    private String publisher;
    private List<String> genre;
    @JsonDeserialize(using = StringCollectionCustomDeserializer.class)
    private List<PlayMode> playMode;
    @JsonDeserialize(using = StringCollectionCustomDeserializer.class)
    private List<GamePlatform> gamePlatform;
    private Author author;
    private AggregateRating aggregateRating;
    private LocalDate releaseDate;

    public record Author(String name) {
    }

    public record AggregateRating(BigDecimal ratingValue,
                                   int ratingCount) {
    }

}
