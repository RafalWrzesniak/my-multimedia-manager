package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.*;

@Service
@RequiredArgsConstructor
public class UserObjectDetailsFounder {

    private final BookUserDetailsRepository bookUserDetailsRepository;
    private final MovieUserDetailsRepository movieUserDetailsRepository;

    public MovieWithUserDetailsDto findDetailedMovieDataFor(User user, Movie movie) {
        MovieUserDetails details = movieUserDetailsRepository.findById(MovieUserId.of(movie, user)).orElse(MovieUserDetails.empty());
        return MovieWithUserDetailsDto.of(movie, details);
    }

    public MovieListWithUserDetails findDetailedMovieDataFor(MovieContentList movieContentList, User user) {
        return MovieListWithUserDetails.builder()
                .id(movieContentList.getId())
                .name(movieContentList.getName())
                .isToWatchList(movieContentList.isToWatchList())
                .isRecentlyWatchedList(movieContentList.isRecentlyWatchedList())
                .movieWithUserDetailsDtos(movieContentList.getContentList().stream()
                        .map(movie -> findDetailedMovieDataFor(user, movie))
                        .toList())
                .build();
    }

    public BookWithUserDetailsDto findDetailedBookDataFor(User user, Book book) {
        BookUserDetails details = bookUserDetailsRepository.findById(BookUserId.of(book, user)).orElse(BookUserDetails.empty());
        return BookWithUserDetailsDto.of(book, details);
    }

    public BookListWithUserDetails findDetailedBookDataFor(BookContentList bookContentList, User user) {
        return BookListWithUserDetails.builder()
                .id(bookContentList.getId())
                .name(bookContentList.getName())
                .bookWithUserDetailsDtos(bookContentList.getContentList().stream()
                        .map(book -> findDetailedBookDataFor(user, book))
                        .toList())
                .build();
    }

}
