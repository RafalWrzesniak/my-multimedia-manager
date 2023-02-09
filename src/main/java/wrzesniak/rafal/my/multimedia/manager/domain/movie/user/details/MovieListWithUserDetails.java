package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MovieListWithUserDetails {

    long id;
    String name;
    List<MovieWithUserDetailsDto> movieWithUserDetailsDtos;
    boolean isToWatchList;
    boolean isRecentlyWatchedList;

}
