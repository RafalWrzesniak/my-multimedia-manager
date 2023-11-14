package wrzesniak.rafal.my.multimedia.manager.domain.movie.objects;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.ActorInMovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.SingleFieldDto;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebSearchable;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Data
@With
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto implements FilmwebSearchable {

    private String id;
    private String title;
    private String originalTitle;
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

    private WikipediaData wikipedia;

    @Override
    public String getFilmwebSearchString() {
        String titleToSearch = originalTitle != null && !originalTitle.isEmpty() ? originalTitle : title;
        return String.format("%s (%s)", titleToSearch, releaseDate.getYear());
    }

    public record WikipediaData(String titleInLanguage) {

        public String titleInLanguage() {
            String filmSuffix = " (film)";
            if(titleInLanguage != null && titleInLanguage.contains(filmSuffix)) {
                return titleInLanguage.substring(0, titleInLanguage.lastIndexOf(filmSuffix));
            }
            return titleInLanguage;
        }

    }
}
