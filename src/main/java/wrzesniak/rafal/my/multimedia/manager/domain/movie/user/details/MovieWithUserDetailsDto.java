package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.Builder;
import lombok.Value;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

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
    List<Actor> actorList;
    List<Actor> directorList;
    List<Actor> writerList;
    List<String> genreList;
    List<String> countryList;
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
                .actorList(movie.getActorList())
                .directorList(movie.getDirectorList())
                .writerList(movie.getWriterList())
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
