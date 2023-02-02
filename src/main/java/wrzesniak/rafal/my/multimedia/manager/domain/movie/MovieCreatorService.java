package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.aop.TrackExecutionTime;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Role;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.ActorInMovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb.FilmwebMovieUrl;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb.ImdbId;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebService;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import javax.validation.Valid;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MovieCreatorService {

    private static final int ACTORS_TO_DOWNLOAD = 10;
    private static final int CREW_TO_DOWNLOAD = 4;

    private final ImdbService imdbService;
    private final WebOperations webOperations;
    private final FilmwebService filmwebService;
    private final MovieRepository movieRepository;
    private final ActorCreatorService actorCreatorService;

    @Transactional
    public Optional<Movie> createMovieFromFilmwebUrl(@Valid @FilmwebMovieUrl URL filmwebMovieUrl) {
        Optional<Movie> movieInDatabase = movieRepository.findByFilmwebUrl(filmwebMovieUrl);
        if(movieInDatabase.isPresent()) {
            log.info("Movie with url `{}` already exists in database: {}", filmwebMovieUrl, movieInDatabase);
            return movieInDatabase;
        }
        String polishTitle = filmwebService.findTitleFromUrl(filmwebMovieUrl);
        if(polishTitle == null) {
            log.warn("Cannot find movie title basing on provided url: {}", filmwebMovieUrl);
            return Optional.empty();
        }
        return createMovieFromPolishTitle(polishTitle);
    }

    @Transactional
    @TrackExecutionTime
    public Optional<Movie> createMovieFromPolishTitle(String polishTitle) {
        Optional<MovieDto> foundByTitle = imdbService.findBestMovieForSearchByTitle(polishTitle);
        return foundByTitle.flatMap(movieDto -> createMovieFromImdbId(movieDto.getId()));
    }

    @Transactional
    @TrackExecutionTime
    public Optional<Movie> createMovieFromImdbId(@Valid @ImdbId String imdbId) {
        Optional<Movie> movieInDataBase = movieRepository.findByImdbId(imdbId);
        if(movieInDataBase.isPresent()) {
            log.info("Movie with imdb id `{}` already exists in database: {}", imdbId, movieInDataBase.get());
            return movieInDataBase;
        }
        MovieDto movieDto = imdbService.getMovieById(imdbId);
        Movie movie = DtoMapper.mapToMovie(movieDto);
        webOperations.downloadResizedImageTo(movieDto.getImage(), movie.getImagePath());
        formatPlotLocal(movie);
        addFullCastToMovie(movie, movieDto);
        Movie savedMovie = movieRepository.save(movie);
        log.info("Movie saved in database: {}", savedMovie);
        return Optional.of(savedMovie);
    }

    private void formatPlotLocal(Movie movie) {
        String plotLocal = movie.getPlotLocal();
        if(plotLocal.matches(".+? \\[.+?\\]$")) {
            String formattedPlotLocal = plotLocal.substring(0, plotLocal.lastIndexOf(" ["));
            movie.setPlotLocal(formattedPlotLocal);
        }
    }

    private void addFullCastToMovie(Movie movie, MovieDto movieDto) {
        log.info("Adding cast for movie {}", movie);
        addActorsToMovie(movie, movieDto.getActorList(), Role.Actor);
        log.info("Adding directors for movie {}", movie);
        addActorsToMovie(movie, movieDto.getDirectorList(), Role.Director);
        log.info("Adding writers for movie {}", movie);
        addActorsToMovie(movie, movieDto.getWriterList(), Role.Writer);
    }

    private void addActorsToMovie(Movie movie, List<ActorInMovieDto> actorsInMovieDtos, Role actorsRole) {
        int actorsNumberToDownload = Role.Actor.equals(actorsRole) ? ACTORS_TO_DOWNLOAD : CREW_TO_DOWNLOAD;
        log.info("Trying to download and add with Role `{}` actors: {}", actorsRole, actorsInMovieDtos);
        List<Actor> actors = createActorsFromActorsInMovieDto(actorsInMovieDtos, actorsNumberToDownload);
        movie.addActorsWithRole(actors, actorsRole);
    }

    private List<Actor> createActorsFromActorsInMovieDto(List<ActorInMovieDto> actorInMovieDtos, long actorsNumber) {
        return actorInMovieDtos.stream()
                .map(ActorInMovieDto::id)
                .map(actorCreatorService::createActorFromImdbId)
                .filter(Optional::isPresent)
                .limit(actorsNumber)
                .map(Optional::get)
                .toList();
    }

}
