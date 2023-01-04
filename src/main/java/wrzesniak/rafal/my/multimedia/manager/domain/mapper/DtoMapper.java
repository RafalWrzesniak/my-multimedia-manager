package wrzesniak.rafal.my.multimedia.manager.domain.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static wrzesniak.rafal.my.multimedia.manager.domain.user.UserRole.USER;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class DtoMapper {

    public static Actor mapToActor(ActorDto actorDto) {
        return Actor.builder()
                .imdbId(actorDto.getId())
                .name(actorDto.getName())
                .birthDate(actorDto.getBirthDate())
                .deathDate(actorDto.getDeathDate())
                .filmwebUrl(actorDto.getFilmwebUrl())
                .playedInMovies(new ArrayList<>())
                .directedMovies(new ArrayList<>())
                .wroteMovies(new ArrayList<>())
                .build();
    }

    public static Movie mapToMovie(MovieDto movieDto) {
        log.info("Trying to map dto to movie: {}", movieDto);
        return Movie.builder()
                .imdbId(movieDto.getId())
                .title(movieDto.getTitle())
                .filmwebUrl(movieDto.getFilmwebUrl())
                .polishTitle(movieDto.getPolishTitle())
                .releaseDate(movieDto.getReleaseDate())
                .runtimeMins(movieDto.getRuntimeMins())
                .imDbRating(movieDto.getImDbRating())
                .imDbRatingVotes(movieDto.getImDbRatingVotes())
                .genreList(getAsStrings(movieDto.getGenreList()))
                .countryList(getAsStrings(movieDto.getCountryList()))
                .plotLocal(movieDto.getPlotLocal())
                .actorList(new ArrayList<>())
                .directorList(new ArrayList<>())
                .writerList(new ArrayList<>())
                .build();
    }

    public static User mapToUser(LoginCredentials credentials) {
        return User.builder()
                .username(credentials.getUsername())
                .password(credentials.getPassword())
                .userRole(USER)
                .enabled(true)
                .movieLists(new ArrayList<>())
                .actorList(new ArrayList<>())
                .build();
    }

    public static List<String> getAsStrings(List<SingleFieldDto> singleFieldDtos) {
        return singleFieldDtos.stream()
                .map(SingleFieldDto::value)
                .toList();
    }

}
