package wrzesniak.rafal.my.multimedia.manager.domain.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.AuthorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.ISBN;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.ActorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static wrzesniak.rafal.my.multimedia.manager.domain.user.UserRole.USER;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.withRemovedDate;

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
                .createdOn(LocalDate.now())
                .build();
    }

    public static Movie mapToMovie(MovieDto movieDto) {
        log.info("Trying to map dto to movie: {}", movieDto);
        return Movie.builder()
                .imdbId(movieDto.getId())
                .title(firstNotEmpty(movieDto.getOriginalTitle(), movieDto.getTitle()))
                .filmwebUrl(movieDto.getFilmwebUrl())
                .polishTitle(withRemovedDate(movieDto.getWikipedia().titleInLanguage()))
                .releaseDate(movieDto.getReleaseDate())
                .runtimeMins(movieDto.getRuntimeMins())
                .imDbRating(BigDecimal.valueOf(movieDto.getImDbRating()).setScale(1, RoundingMode.HALF_UP))
                .imDbRatingVotes(movieDto.getImDbRatingVotes())
                .genreList(getAsStrings(movieDto.getGenreList()))
                .countryList(getAsStrings(movieDto.getCountryList()))
                .plotLocal(movieDto.getPlotLocal())
                .actorList(new HashSet<>())
                .directorList(new HashSet<>())
                .writerList(new HashSet<>())
                .createdOn(LocalDate.now())
                .build();
    }

    public static User mapToUser(LoginCredentials credentials) {
        return User.builder()
                .username(credentials.getUsername())
                .password(credentials.getPassword())
                .userRole(USER)
                .enabled(true)
                .movieLists(new ArrayList<>())
                .bookLists(new ArrayList<>())
                .gameLists(new ArrayList<>())
                .build();
    }

    public static Set<String> getAsStrings(List<SingleFieldDto> singleFieldDtos) {
        return singleFieldDtos.stream()
                .map(SingleFieldDto::value)
                .collect(Collectors.toSet());
    }

    private static String firstNotEmpty(String title1, String title2) {
        return title1 != null && !title1.isEmpty() ? title1 : title2;
    }

    public static Book mapToBook(BookDto bookDto) {
        return Book.builder()
                .title(bookDto.getName())
                .category(bookDto.getGenre().substring(bookDto.getGenre().lastIndexOf("/") + 1))
                .description(bookDto.getDescription())
                .publisher(bookDto.getPublisher())
                .numberOfPages(bookDto.getNumberOfPages())
                .isbn(ISBN.of(bookDto.getIsbn()))
                .lubimyCzytacUrl(toURL(bookDto.getUrl()))
                .datePublished(bookDto.getDatePublished())
                .series(bookDto.getSeries())
                .createdOn(LocalDate.now())
                .build();
    }

    public static Author mapToAuthor(AuthorDto authorDto) {
        return Author.builder()
                .name(authorDto.getName())
                .writtenBooks(new ArrayList<>())
                .createdOn(LocalDate.now())
                .build();
    }

    public static Game mapToGame(GameDto gameDto) {
        return Game.builder()
                .title(gameDto.getName())
                .gryOnlineUrl(gameDto.getUrl())
                .description(gameDto.getDescription())
                .ratingValue(gameDto.getAggregateRating().ratingValue())
                .ratingCount(gameDto.getAggregateRating().ratingCount())
                .studio(gameDto.getAuthor().name())
                .publisher(gameDto.getPublisher())
                .playModes(new HashSet<>(gameDto.getPlayMode()))
                .gamePlatform(new HashSet<>(gameDto.getGamePlatform()))
                .genreList(new HashSet<>(gameDto.getGenre()))
                .releaseDate(gameDto.getReleaseDate())
                .build();
    }
}
