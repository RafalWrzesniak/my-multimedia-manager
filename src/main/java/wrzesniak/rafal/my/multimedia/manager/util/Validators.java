package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NotValidFilmwebUrlException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NotValidImdbIdException;

import java.net.URL;

@Slf4j
@Component
public class Validators {

    public void validateImdbId(String imdbId) {
        if(!imdbId.matches("^(nm)|(tt)\\d{5,9}$")) {
            throw new NotValidImdbIdException();
        }
    }

    public void validateFilmwebMovieUrl(URL url) {
        if(!url.toString().matches("^https://www\\.filmweb\\.pl/film/.+")) {
            throw new NotValidFilmwebUrlException();
        }
    }

}
