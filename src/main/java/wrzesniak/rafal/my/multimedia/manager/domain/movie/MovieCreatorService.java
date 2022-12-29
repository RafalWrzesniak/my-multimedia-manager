package wrzesniak.rafal.my.multimedia.manager.domain.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wrzesniak.rafal.my.multimedia.manager.aop.TrackExecutionTime;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorManagementService;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.Role;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.ActorInMovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.util.Validators;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;
import wrzesniak.rafal.my.multimedia.manager.web.filmweb.FilmwebService;
import wrzesniak.rafal.my.multimedia.manager.web.imdb.ImdbService;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieCreatorService {

    private static final int ACTORS_TO_DOWNLOAD = 10;
    private static final int CREW_TO_DOWNLOAD = 4;

    private final Validators validators;
    private final ImdbService imdbService;
    private final WebOperations webOperations;
    private final FilmwebService filmwebService;
    private final MovieRepository movieRepository;
    private final ActorManagementService actorManagementService;

    @Transactional
    public Optional<Movie> createMovieFromFilmwebUrl(URL filmwebMovieUrl) {
        validators.validateFilmwebMovieUrl(filmwebMovieUrl);
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
        Optional<MovieDto> optionalBestMovieForSearchByTitle = imdbService.findBestMovieForSearchByTitle(polishTitle);
        if(optionalBestMovieForSearchByTitle.isEmpty()) {
            return Optional.empty();
        }
        MovieDto movieDto = optionalBestMovieForSearchByTitle.orElseThrow();
        Optional<Movie> movieInDataBase = movieRepository.findByImdbId(movieDto.getId());
        if(movieInDataBase.isPresent()) {
            log.info("Movie with title `{}` already exists in database: {}", polishTitle, movieInDataBase.get());
            return movieInDataBase;
        }
        Movie movie = DtoMapper.mapToMovie(movieDto);
        filmwebService.addFilmwebUrlTo(movie);
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
                .map(actorManagementService::createActorFromImdbId)
                .filter(Optional::isPresent)
                .limit(actorsNumber)
                .map(Optional::get)
                .toList();
    }

}
