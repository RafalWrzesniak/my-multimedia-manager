package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserObjectDetailsFounder {

    private final BookRepository bookRepository;
    private final MovieRepository movieRepository;
    private final BookUserDetailsRepository bookUserDetailsRepository;
    private final MovieUserDetailsRepository movieUserDetailsRepository;

    public MovieWithUserDetailsDto findDetailedMovieDataFor(Movie movie, User user, boolean withActors) {
        MovieUserDetails details = movieUserDetailsRepository.findById(MovieUserId.of(movie, user)).orElse(MovieUserDetails.empty());
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
        BookUserDetails details = bookUserDetailsRepository.findById(BookUserId.of(book, user)).orElse(BookUserDetails.empty());
        return BookWithUserDetailsDto.of(book, details);
    }

    public BookListWithUserDetails findDetailedBookDataFor(BookContentList bookContentList, User user, Pageable pageRequest) {
        BookListWithUserDetails rawList = BookListWithUserDetails.of(bookContentList);
        List<Book> booksFromList = bookRepository.findBooksInContentList(rawList.getId(), pageRequest);
        return rawList.withBookWithUserDetailsDtos(booksFromList.stream()
                        .map(book -> findDetailedBookDataFor(book, user))
                        .toList());
    }

}
