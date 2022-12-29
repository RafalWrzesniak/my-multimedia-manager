package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorRepository;
import wrzesniak.rafal.my.multimedia.manager.util.Validators;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("actor")
@RequiredArgsConstructor
public class ActorController {

    private final ActorRepository actorRepository;
    private final Validators validators;

    @GetMapping("/findByImdbId/{imdbId}")
    public Optional<Actor> getByImdbId(@PathVariable String imdbId) {
        validators.validateImdbId(imdbId);
        return actorRepository.findByImdbId(imdbId);
    }

    @GetMapping("/findById/{id}")
    public Optional<Actor> getById(@PathVariable long id) {
        return actorRepository.findById(id);
    }

    @GetMapping("/")
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

}
