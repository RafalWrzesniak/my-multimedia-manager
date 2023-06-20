package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.repository.BookRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.GameContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.repository.GameRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.repository.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserObjectDetailsFounder {

    private final GameRepository gameRepository;
    private final BookRepository bookRepository;
    private final MovieRepository movieRepository;
    private final GameUserDetailsRepository gameUserDetailsRepository;
    private final BookUserDetailsRepository bookUserDetailsRepository;
    private final MovieUserDetailsRepository movieUserDetailsRepository;

    public MovieWithUserDetailsDto findDetailedMovieDataFor(Movie movie, User user, boolean withActors) {
        MovieUserId movieUserId = MovieUserId.of(movie, user);
        MovieUserDetails details = movieUserDetailsRepository.findById(movieUserId).orElse(new MovieUserDetails(movieUserId));
        return MovieWithUserDetailsDto.of(movie, details, withActors);
    }

    public MovieListWithUserDetails findDetailedMovieDataFor(MovieContentList movieContentList, User user, boolean withActors, Pageable pageRequest) {
        MovieListWithUserDetails rawList = MovieListWithUserDetails.of(movieContentList);
        List<Movie> moviesFromList = movieRepository.findMoviesInContentList(rawList.getId(), pageRequest);
        return rawList.withMovieWithUserDetailsDtos(moviesFromList.stream()
                .map(movie -> findDetailedMovieDataFor(movie, user, withActors))
                .toList());
    }

    public BookWithUserDetailsDto findDetailedBookDataFor(Book book, User user) {
        BookUserId bookUserId = BookUserId.of(book, user);
        BookUserDetails details = bookUserDetailsRepository.findById(bookUserId).orElse(new BookUserDetails(bookUserId));
        return BookWithUserDetailsDto.of(book, details);
    }

    public BookListWithUserDetails findDetailedBookDataFor(BookContentList bookContentList, User user, Pageable pageRequest) {
        BookListWithUserDetails rawList = BookListWithUserDetails.of(bookContentList);
        List<Book> booksFromList = bookRepository.findBooksInContentList(rawList.getId(), pageRequest);
        return rawList.withBookWithUserDetailsDtos(booksFromList.stream()
                        .map(book -> findDetailedBookDataFor(book, user))
                        .toList());
    }

    public GameWithUserDetailsDto findDetailedGameDataFor(Game game, User user) {
        GameUserId gameUserId = GameUserId.of(game, user);
        GameUserDetails details = gameUserDetailsRepository.findById(gameUserId).orElse(new GameUserDetails(gameUserId));
        return GameWithUserDetailsDto.of(game, details);
    }

    public GameListWithUserDetails findDetailedGameDataFor(GameContentList gameContentList, User user, Pageable pageRequest) {
        GameListWithUserDetails rawList = GameListWithUserDetails.of(gameContentList);
        List<Game> gamesFromList = gameRepository.findGamesInContentList(rawList.getId(), pageRequest);
        return rawList.withGameWithUserDetailsDtos(gamesFromList.stream()
                .map(game -> findDetailedGameDataFor(game, user))
                .toList());
    }
}
