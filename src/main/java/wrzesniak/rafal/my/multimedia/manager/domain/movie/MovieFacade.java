package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.service.MovieCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.product.DefaultProductService;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MOVIE_LIST;

@Slf4j
@Service
public class MovieFacade extends DefaultProductService<MovieWithUserDetailsDto, MovieUserDetailsDynamo, MovieListWithUserDetails, MovieDynamo> {

    public MovieFacade(DefaultDynamoRepository<MovieWithUserDetailsDto, MovieUserDetailsDynamo, MovieDynamo> movieDynamoRepository,
                       MovieCreatorService movieCreatorService,
                       ContentListDynamoService contentListDynamoService) {

        super(MOVIE_LIST, MovieListWithUserDetails::of, MovieWithUserDetailsDto::fromSimpleItemAndUserDetails, contentListDynamoService, movieCreatorService, movieDynamoRepository);
    }

}