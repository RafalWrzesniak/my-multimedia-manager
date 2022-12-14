package wrzesniak.rafal.my.multimedia.manager.domain.actor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebService;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorManagementService {

    private final ImdbService imdbService;
    private final WebOperations webOperations;
    private final FilmwebService filmwebService;
    private final ActorRepository actorRepository;

    @Transactional
    public Optional<Actor> createActorFromImdbId(String imdbId) {
        Optional<Actor> actorInDataBase = actorRepository.findByImdbId(imdbId);
        if(actorInDataBase.isPresent()) {
            log.info("Actor with imdbId {} already exists in database: {}", imdbId, actorInDataBase.get());
            return actorInDataBase;
        }
        Optional<ActorDto> optionalActorDto = imdbService.getActorById(imdbId);
        if(optionalActorDto.isEmpty()) {
            return Optional.empty();
        }
        ActorDto actorDto = optionalActorDto.get();
        filmwebService.addFilmwebUrlTo(actorDto);

        Actor actor = DtoMapper.mapToActor(actorDto);
        webOperations.downloadResizedImageTo(actorDto.getImage(), actor.getImagePath());
        if(actor.getFilmwebUrl() == null || actor.getBirthDate() == null) {
            return Optional.empty();
        }
        Actor savedActor = actorRepository.save(actor);
        return Optional.of(savedActor);
    }

}
