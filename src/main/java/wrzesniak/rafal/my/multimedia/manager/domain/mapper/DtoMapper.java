package wrzesniak.rafal.my.multimedia.manager.domain.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.ISBN;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static wrzesniak.rafal.my.multimedia.manager.domain.user.UserRole.USER;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

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
                .polishTitle(movieDto.getWikipedia().titleInLanguage())
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
                .actorList(new ArrayList<>())
                .bookLists(new ArrayList<>())
                .build();
    }

    public static List<String> getAsStrings(List<SingleFieldDto> singleFieldDtos) {
        return singleFieldDtos.stream()
                .map(SingleFieldDto::value)
                .toList();
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
                .bookFormat(bookDto.getBookFormat() != null ? bookDto.getBookFormat() : BookFormat.Paper)
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
}
