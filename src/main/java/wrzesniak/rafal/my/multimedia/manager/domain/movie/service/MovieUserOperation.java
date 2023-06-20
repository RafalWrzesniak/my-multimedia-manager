package wrzesniak.rafal.my.multimedia.manager.domain.movie.service;

import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductUserOperations;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BaseContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserId;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.user.User;

import java.util.List;

@Component
public class MovieUserOperation implements ProductUserOperations<MovieWithUserDetailsDto, Movie, MovieUserDetails, MovieListWithUserDetails> {

    @Override
    public MovieWithUserDetailsDto mergeProductWithUserDetails(Movie movie, MovieUserDetails movieUserDetails) {
        return MovieWithUserDetailsDto.of(movie, movieUserDetails, false);
    }

    @Override
    public MovieListWithUserDetails createDetailedListFrom(BaseContentList<Movie> contentList) {
        return MovieListWithUserDetails.of((MovieContentList) contentList);
    }

    @Override
    public MovieListWithUserDetails addDetailedProductsToDetailedList(MovieListWithUserDetails list, List<MovieWithUserDetailsDto> movies) {
        return list.withMovieWithUserDetailsDtos(movies);
    }

    @Override
    public ProductUserId getProductUserIdFrom(Movie movie, User user) {
        return MovieUserId.of(movie, user);
    }

    @Override
    public MovieUserDetails createNewProductUserDetails(ProductUserId productUserId) {
        return new MovieUserDetails(productUserId);
    }
}
