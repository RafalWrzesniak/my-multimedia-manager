package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.ActorInMovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.SingleFieldDto;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebSearchable;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Data
@With
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto implements ImdbObject, FilmwebSearchable {

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

    private URL filmwebUrl;

    @Override
    public String getFilmwebSearchString() {
        return String.format("%s (%s)", title, releaseDate.getYear());
    }
}
