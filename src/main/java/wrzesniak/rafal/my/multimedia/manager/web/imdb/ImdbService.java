package wrzesniak.rafal.my.multimedia.manager.web.imdb;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import wrzesniak.rafal.my.multimedia.manager.domain.actor.ActorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieDto;
import wrzesniak.rafal.my.multimedia.manager.util.StringFunctions;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.slash;

@Slf4j
@Service
public class ImdbService {

    private final WebClient client;
    private final ImdbConfiguration imdbConfiguration;

    public ImdbService(ImdbConfiguration imdbConfiguration) {
        this.imdbConfiguration = imdbConfiguration;
        this.client = WebClient.create(imdbConfiguration.getUrlPl());
    }

    public MovieDto getMovieById(String id) {
        return retrievePathFromApi(imdbConfiguration.getMovieApi(), id + slash(imdbConfiguration.getWikipedia()))
                .bodyToMono(MovieDto.class)
                .block();
    }

    public Optional<ActorDto> getActorById(String id) {
        ActorDto actorDto = retrievePathFromApi(imdbConfiguration.getActorApi(), id)
                .bodyToMono(ActorDto.class)
                .block();
        return notFoundInImdb(actorDto) ? Optional.empty() : Optional.of(actorDto);
    }

    public Optional<MovieDto> findBestMovieForSearchByTitle(String title) {
        List<Result> bestFoundMovies = findPossibleMoviesByTitle(title);
        if(bestFoundMovies.isEmpty()) {
            return Optional.empty();
        }
        String bestMovieId = bestFoundMovies.get(0).id();
        MovieDto movieDto = getMovieById(bestMovieId);
        log.info("For query {} a MovieDTO was chosen as the best choice: {}", title, movieDto);
        return Optional.of(movieDto);
    }

    public List<Result> findPossibleMoviesByTitle(String title) {
        QueryResult queryResult = retrievePathFromApi(imdbConfiguration.getSearchApi(), StringFunctions.withRemovedSlashes(title))
                .bodyToMono(QueryResult.class)
                .block();
        if(notFoundInImdb(queryResult)) {
            return List.of();
        }
        List<Result> bestResults = queryResult.results.stream()
                .limit(4)
                .toList();
        log.info("For query `{}` found possible results: {}", title, bestResults);
        return bestResults;
    }

    @SneakyThrows
    public URL findResizedImageUrl(URL originalImageUrl) {
        String resizeApi = imdbConfiguration.getResizeApi();
        String apiKey = imdbConfiguration.getApiKey();
        String imageSize = imdbConfiguration.getImageSize();
        String apiUrl = imdbConfiguration.getUrl();
        String uri = apiUrl+slash(resizeApi)+"?ApiKey="+apiKey+"&size="+imageSize+"&url="+originalImageUrl;
        return new URL(uri);
    }

    private ResponseSpec retrievePathFromApi(String API, String path) {
        return client.get().uri(slash(API) + slash(imdbConfiguration.getApiKey()) + slash(path)).retrieve();
    }

    private boolean notFoundInImdb(ImdbObject imdbObject) {
        return imdbObject == null || imdbConfiguration.getNotFound().equals(imdbObject.getErrorMessage());
    }


    private record QueryResult(String searchType,
                               String expression,
                               List<Result> results,
                               String errorMessage) implements ImdbObject {

        public String getErrorMessage(){
            return errorMessage;
        }
    }

    private record Result(String id,
                          String title,
                          String description) {}

}
