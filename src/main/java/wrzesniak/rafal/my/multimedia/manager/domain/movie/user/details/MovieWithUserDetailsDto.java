package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.SeriesInfo;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@With
@Value
@Builder
public class MovieWithUserDetailsDto {

    String id;
    String imdbId;
    String title;
    String polishTitle;
    LocalDate releaseDate;
    Integer runtimeMins;
    BigDecimal imDbRating;
    Integer imDbRatingVotes;
    URL filmwebUrl;
    Set<String> genreList;
    Set<String> countryList;
    String plotLocal;
    LocalDateTime createdOn;
    LocalDateTime updatedOn;
    LocalDate watchedOn;
    String webImageUrl;
    SeriesInfo seriesInfo;

    public static MovieWithUserDetailsDto of(MovieDynamo movieDynamo, MovieUserDetailsDynamo movieUserDetailsDynamo) {
        return MovieWithUserDetailsDto.builder()
                .id(movieDynamo.getMovieId())
                .title(movieDynamo.getTitle())
                .polishTitle(movieDynamo.getPolishTitle())
                .releaseDate(movieDynamo.getReleaseDate())
                .runtimeMins(movieDynamo.getRuntimeMins())
                .imDbRating(movieDynamo.getRating())
                .imDbRatingVotes(movieDynamo.getRatingVotes())
                .filmwebUrl(movieDynamo.getFilmwebUrl())
                .genreList(movieDynamo.getGenreList())
                .countryList(movieDynamo.getCountryList())
                .plotLocal(movieDynamo.getPlotLocal())
                .createdOn(movieUserDetailsDynamo.getCreatedOn())
                .updatedOn(movieUserDetailsDynamo.getUpdatedOn())
                .watchedOn(movieUserDetailsDynamo.getWatchedOn())
                .webImageUrl(movieDynamo.getWebImageUrl())
                .seriesInfo(movieDynamo.getSeriesInfo())
                .build();
    }

}
