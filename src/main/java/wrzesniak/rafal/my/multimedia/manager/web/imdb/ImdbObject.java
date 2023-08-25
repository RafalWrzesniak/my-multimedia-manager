package wrzesniak.rafal.my.multimedia.manager.web.imdb;

import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;

import java.net.URL;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.slash;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

public interface ImdbObject {

    String IMDB_BASE_URL = "https://www.imdb.com";

    String getImdbId();

    default URL getImdbUrl() {
        if(getImdbId() == null) {
            return null;
        }
        String titleOrPerson = getClass().getSimpleName().equals(Movie.class.getSimpleName()) ? "title" : "person";
        return toURL(IMDB_BASE_URL + slash(titleOrPerson) + slash(getImdbId()));
    }

}
