package wrzesniak.rafal.my.multimedia.manager.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.aop.TrackExecutionTime;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebMovieCreator;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MovieCreatorService implements ProductCreatorService<MovieWithUserDetailsDto> {

    private final FilmwebMovieCreator filmwebMovieCreator;
    private final DefaultDynamoRepository<MovieWithUserDetailsDto, MovieUserDetailsDynamo, MovieDynamo> movieDynamoRepository;

    @Override
    @TrackExecutionTime
    public MovieWithUserDetailsDto createProductFromUrl(URL filmwebMovieUrl, String username) {
        Optional<MovieWithUserDetailsDto> movieInDatabase = movieDynamoRepository.getById(filmwebMovieUrl.toString(), username);
        if(movieInDatabase.isPresent()) {
            log.info("Movie with url `{}` already exists in database: {}", filmwebMovieUrl, movieInDatabase);
            movieDynamoRepository.createOrUpdateUserDetailsFor(filmwebMovieUrl.toString(), username);
            return movieInDatabase.get();
        }
        MovieDynamo movie = filmwebMovieCreator.createMovieFromUrl(filmwebMovieUrl);
        MovieWithUserDetailsDto savedMovie = movieDynamoRepository.saveProduct(movie, username);
        log.info("Movie saved in database: {}", savedMovie);
        return savedMovie;
    }

}
