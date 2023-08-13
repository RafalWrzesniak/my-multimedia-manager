package wrzesniak.rafal.my.multimedia.manager.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import wrzesniak.rafal.my.multimedia.manager.aop.TrackExecutionTime;
import wrzesniak.rafal.my.multimedia.manager.domain.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.ActorInMovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Actor;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.ActorCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.Role;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.repository.MovieRepository;
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
public class MovieCreatorService implements ProductCreatorService<Movie> {

    private static final int ACTORS_TO_DOWNLOAD = 10;
    private static final int CREW_TO_DOWNLOAD = 4;

    private final ImdbService imdbService;
    private final WebOperations webOperations;
    private final FilmwebService filmwebService;
    private final MovieRepository movieRepository;
    private final ActorCreatorService actorCreatorService;

    @Override
    @Transactional
    @TrackExecutionTime
    public Movie createProductFromUrl(URL filmwebMovieUrl) {
        Optional<Movie> movieInDatabase = movieRepository.findByFilmwebUrl(filmwebMovieUrl);
        if(movieInDatabase.isPresent()) {
            log.info("Movie with url `{}` already exists in database: {}", filmwebMovieUrl, movieInDatabase);
            return movieInDatabase.get();
        }
        String polishTitle = filmwebService.findTitleFromUrl(filmwebMovieUrl);
        if(polishTitle == null) {
            log.warn("Cannot find movie title basing on provided url: {}", filmwebMovieUrl);
            throw new IllegalArgumentException("Cannot find polish title in this url");
        }
        return createMovieFromPolishTitle(polishTitle, filmwebMovieUrl);
    }

    @Transactional
    @TrackExecutionTime
    public Movie createMovieFromPolishTitle(String polishTitle, URL filmwebUrl) {
        Optional<MovieDto> foundByTitle = imdbService.findBestMovieForSearchByTitle(polishTitle);
        return foundByTitle
                .map(movieDto -> createMovieFromImdbId(movieDto.getId(), filmwebUrl))
                .orElseThrow();
    }

    @Transactional
    @TrackExecutionTime
    public Movie createMovieFromImdbId(@Valid @ImdbId String imdbId, URL filmwebUrl) {
        Optional<Movie> movieInDataBase = movieRepository.findByImdbId(imdbId);
        if(movieInDataBase.isPresent()) {
            log.info("Movie with imdb id `{}` already exists in database: {}", imdbId, movieInDataBase.get());
            return movieInDataBase.get();
        }
        MovieDto movieDto = imdbService.getMovieById(imdbId);
        Movie movie = DtoMapper.mapToMovie(movieDto);
        movie.getImagePath().forEach(imagePath -> webOperations.downloadResizedImageTo(movieDto.getImage(), imagePath));
        formatPlotLocal(movie);
        addFullCastToMovie(movie, movieDto);
        movie.setFilmwebUrl(filmwebUrl);
        Movie savedMovie = movieRepository.save(movie);
        log.info("Movie saved in database: {}", savedMovie);
        return savedMovie;
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
