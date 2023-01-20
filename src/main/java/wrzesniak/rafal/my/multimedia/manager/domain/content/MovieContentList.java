package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.Movie;

import javax.persistence.Entity;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.MovieList;

@With
@Getter
@Setter
@Entity
@AllArgsConstructor
public class MovieContentList extends BaseContentList<Movie> {

    public MovieContentList(String listName) {
        super(listName, MovieList);
    }

    private boolean isRecentlyWatchedList;
    private boolean isToWatchList;

}
