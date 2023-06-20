package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;

import javax.persistence.Entity;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MOVIE_LIST;

@With
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MovieContentList extends BaseContentList<Movie> {

    public MovieContentList(String listName) {
        super(listName, MOVIE_LIST);
    }

    private boolean isRecentlyWatchedList;
    private boolean isToWatchList;

}
