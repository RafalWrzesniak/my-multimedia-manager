package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;

import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MOVIE_LIST;

@With
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieListWithUserDetails {

    String id;
    String name;
    int moviesNumber;
    boolean isToWatchList;
    ContentListType listType;
    boolean isAllMoviesList;
    boolean isRecentlyWatchedList;
    List<MovieWithUserDetailsDto> movieWithUserDetailsDtos;

    public static MovieListWithUserDetails of(MovieContentList movieContentList) {
        return MovieListWithUserDetails.builder()
                .id(movieContentList.getId().toString())
                .name(movieContentList.getName())
                .isToWatchList(movieContentList.isToWatchList())
                .isRecentlyWatchedList(movieContentList.isRecentlyWatchedList())
                .moviesNumber(movieContentList.getContentList().size())
                .isAllMoviesList(movieContentList.isAllContentList())
                .listType(MOVIE_LIST)
                .build();
    }

    public static MovieListWithUserDetails of(ContentListDynamo contentListDynamo, List<MovieWithUserDetailsDto> movieDtos) {
        return MovieListWithUserDetails.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .isAllMoviesList(contentListDynamo.isAllContentList())
                .moviesNumber(movieDtos.size())
                .listType(MOVIE_LIST)
                .movieWithUserDetailsDtos(movieDtos)
                .build();
    }
}
