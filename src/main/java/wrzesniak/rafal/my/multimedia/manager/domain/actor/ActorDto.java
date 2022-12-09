package wrzesniak.rafal.my.multimedia.manager.domain.actor;

import lombok.Data;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbObject;

import java.net.URL;
import java.time.LocalDate;

@Data
public class ActorDto implements ImdbObject {

    private String id;
    private String name;
    private String errorMessage;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private URL image;

}
