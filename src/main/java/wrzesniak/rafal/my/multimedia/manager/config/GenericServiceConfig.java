package wrzesniak.rafal.my.multimedia.manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import wrzesniak.rafal.my.multimedia.manager.domain.GenericUserObjectDetailsFounder;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.book.repository.BookRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.service.BookUserOperation;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.game.repository.GameRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.service.GameUserOperation;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.repository.MovieRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.service.MovieUserOperation;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetailsRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;

@Configuration
@RequiredArgsConstructor
public class GenericServiceConfig {

    private final BookRepository bookRepository;
    private final BookUserOperation bookUserOperation;
    private final BookUserDetailsRepository bookUserDetailsRepository;

    private final GameRepository gameRepository;
    private final GameUserOperation gameUserOperation;
    private final GameUserDetailsRepository gameUserDetailsRepository;

    private final MovieRepository movieRepository;
    private final MovieUserOperation movieUserOperation;
    private final MovieUserDetailsRepository movieUserDetailsRepository;


    @Bean
    public GenericUserObjectDetailsFounder<BookWithUserDetailsDto, Book, BookUserDetails, BookListWithUserDetails> userBookDetailsFounder() {
        return new GenericUserObjectDetailsFounder<>(bookUserDetailsRepository, bookRepository, bookUserOperation);
    }

    @Bean
    public GenericUserObjectDetailsFounder<GameWithUserDetailsDto, Game, GameUserDetails, GameListWithUserDetails> userGameDetailsFounder() {
         return new GenericUserObjectDetailsFounder<>(gameUserDetailsRepository, gameRepository, gameUserOperation);
    }

    @Bean
    public GenericUserObjectDetailsFounder<MovieWithUserDetailsDto, Movie, MovieUserDetails, MovieListWithUserDetails> userMovieDetailsFounder() {
        return new GenericUserObjectDetailsFounder<>(movieUserDetailsRepository, movieRepository, movieUserOperation);
    }


    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        return filter;
    }
}
