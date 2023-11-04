package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("movie")
public class MovieController extends BaseProductController<MovieWithUserDetailsDto, MovieUserDetailsDynamo, MovieListWithUserDetails, MovieDynamo> {

    public MovieController(MovieFacade movieFacade) {
        super(movieFacade);
    }
}
