package wrzesniak.rafal.my.multimedia.manager.domain.movie.actor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebSearchable;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbDtoObject;

import java.net.URL;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorDto implements ImdbDtoObject, FilmwebSearchable {

    private String id;
    private String name;
    private String errorMessage;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private URL image;
    private URL filmwebUrl;

    @Override
    public String getFilmwebSearchString() {
        return name;
    }

}
