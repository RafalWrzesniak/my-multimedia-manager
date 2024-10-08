package wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;

import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MOVIE_LIST;

@With
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovieListWithUserDetails {

    @EqualsAndHashCode.Include
    String id;
    String name;
    int productsNumber;
    boolean allMoviesList;
    ContentListType listType;
    List<MovieWithUserDetailsDto> movieWithUserDetailsDtos;

    public static MovieListWithUserDetails of(ContentListDynamo contentListDynamo, List<MovieWithUserDetailsDto> movieDtos, int productsNumber) {
        return MovieListWithUserDetails.builder()
                .id(contentListDynamo.getListId())
                .name(contentListDynamo.getListName())
                .allMoviesList(contentListDynamo.isAllContentList())
                .productsNumber(productsNumber)
                .listType(MOVIE_LIST)
                .movieWithUserDetailsDtos(movieDtos)
                .build();
    }
}
