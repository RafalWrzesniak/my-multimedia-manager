package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Set;

@With
@Value
@Builder
public class MovieWithUserDetailsDto {

    Long id;
    String imdbId;
    String title;
    String polishTitle;
    LocalDate releaseDate;
    Integer runtimeMins;
    BigDecimal imDbRating;
    Integer imDbRatingVotes;
    URL filmwebUrl;
    Set<Actor> actorList;
    Set<Actor> directorList;
    Set<Actor> writerList;
    Set<String> genreList;
    Set<String> countryList;
    String plotLocal;
    LocalDate createdOn;
    LocalDate watchedOn;

    public static MovieWithUserDetailsDto of(Movie movie, MovieUserDetails details, boolean withActors) {
        MovieWithUserDetailsDto detailsDto = MovieWithUserDetailsDto.builder()
                .id(movie.getId())
                .imdbId(movie.getImdbId())
                .title(movie.getTitle())
                .polishTitle(movie.getPolishTitle())
                .releaseDate(movie.getReleaseDate())
                .runtimeMins(movie.getRuntimeMins())
                .imDbRating(movie.getImDbRating())
                .imDbRatingVotes(movie.getImDbRatingVotes())
                .filmwebUrl(movie.getFilmwebUrl())
                .genreList(movie.getGenreList())
                .countryList(movie.getCountryList())
                .plotLocal(movie.getPlotLocal())
                .createdOn(movie.getCreatedOn())
                .watchedOn(details.getWatchedOn())
                .build();
        if(withActors) {
            detailsDto = detailsDto
                    .withActorList(movie.getActorList())
                    .withDirectorList(movie.getDirectorList())
                    .withWriterList(movie.getWriterList());
        }
        return detailsDto;
    }
}
