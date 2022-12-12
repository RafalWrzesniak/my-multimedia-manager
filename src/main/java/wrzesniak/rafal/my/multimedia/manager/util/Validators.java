package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;

@Slf4j
@Component
public class Validators {

    public boolean isValidImdbId(String string) {
        return string.matches("^(nm)|(tt)\\d{5,9}$");
    }

    public boolean isValidFilmwebMovieUrl(URL url) {
        return url.toString().matches("^https://www\\.filmweb\\.pl/film/.+");
    }

}
