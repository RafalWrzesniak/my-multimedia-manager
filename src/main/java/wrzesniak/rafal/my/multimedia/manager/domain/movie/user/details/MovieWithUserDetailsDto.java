package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;

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
    Set<Actor> actorList;
    Set<Actor> directorList;
    Set<Actor> writerList;
    Set<String> genreList;
    Set<String> countryList;
    String plotLocal;
    LocalDateTime createdOn;
    LocalDateTime updatedOn;
    LocalDate watchedOn;
    String webImageUrl;

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
                .build();
    }

    public static MovieWithUserDetailsDto of(Movie movie, MovieUserDetails details, boolean withActors) {
        MovieWithUserDetailsDto detailsDto = MovieWithUserDetailsDto.builder()
                .id(movie.getId().toString())
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
                .createdOn(movie.getCreatedOn().atStartOfDay())
                .watchedOn(details.getWatchedOn())
                .webImageUrl("/" + movie.getImagePath().get(0).subpath(3, 6).toString().replaceAll("\\\\", "/"))
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
