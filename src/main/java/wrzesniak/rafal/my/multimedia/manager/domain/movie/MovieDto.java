package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.ImdbObject;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.ActorInMovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.SingleFieldDto;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MovieDto implements ImdbObject {

    private String id;
    private String title;
    private String polishTitle;
    private LocalDate releaseDate;
    private Integer runtimeMins;
    private double imDbRating;
    private Integer imDbRatingVotes;

    private List<ActorInMovieDto> actorList;
    private List<ActorInMovieDto> directorList;
    private List<ActorInMovieDto> writerList;

    private List<SingleFieldDto> genreList;
    private List<SingleFieldDto> countryList;

    private URL image;
    private String plotLocal;
    private String errorMessage;

}
