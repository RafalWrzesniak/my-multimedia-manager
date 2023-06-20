package wrzesniak.rafal.my.multimedia.manager.web.imdb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.actor.ActorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDto;
import wrzesniak.rafal.my.multimedia.manager.util.StringFunctions;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.slash;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Service
public class ImdbService {

    private final WebClient client;
    private final ImdbConfiguration imdbConfiguration;

    public ImdbService(ImdbConfiguration imdbConfiguration) {
        this.imdbConfiguration = imdbConfiguration;
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        this.client = WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(imdbConfiguration.getUrlPl())
                .build();
    }

    public MovieDto getMovieById(String id) {
        return retrievePathFromApi(imdbConfiguration.getMovieApi(), id + slash(imdbConfiguration.getWikipedia()))
                .bodyToMono(MovieDto.class)
                .filter(dto -> verifyMaxImdbUsage(dto.getErrorMessage()))
                .block();
    }

    public Optional<ActorDto> getActorById(String id) {
        ActorDto actorDto = retrievePathFromApi(imdbConfiguration.getActorApi(), id)
                .bodyToMono(ActorDto.class)
                .filter(dto -> verifyMaxImdbUsage(dto.getErrorMessage()))
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
                .filter(result -> verifyMaxImdbUsage(result.errorMessage()))
                .block();
        if(notFoundInImdb(queryResult)) {
            return List.of();
        }
        List<Result> bestResults = queryResult.results.stream()
                .filter(result -> Movie.class.getSimpleName().equals(result.resultType()) || "Title".equals(result.resultType()))
                .limit(4)
                .toList();
        log.info("For query `{}` found possible results: {}", title, bestResults);
        return bestResults;
    }

    public URL findResizedImageUrl(URL originalImageUrl) {
        String resizeApi = imdbConfiguration.getResizeApi();
        String apiKey = imdbConfiguration.getApiKey();
        String imageSize = imdbConfiguration.getImageSize();
        String apiUrl = imdbConfiguration.getUrl();
        String uri = apiUrl+slash(resizeApi)+"?ApiKey="+apiKey+"&size="+imageSize+"&url="+originalImageUrl;
        return toURL(uri);
    }

    private ResponseSpec retrievePathFromApi(String API, String path) {
        return client.get().uri(slash(API) + slash(imdbConfiguration.getApiKey()) + slash(path)).retrieve();
    }

    private boolean notFoundInImdb(ImdbDtoObject imdbDtoObject) {
        return imdbDtoObject == null || imdbConfiguration.getNotFound().equals(imdbDtoObject.getErrorMessage());
    }

    private boolean verifyMaxImdbUsage(String errorMessage) {
        if(errorMessage != null && errorMessage.contains("Maximum usage")) {
            throw new IllegalStateException("Maximum usage of imdb is reached! Please try tomorrow.");
        }
        return true;
    }

    private record QueryResult(String searchType,
                               String expression,
                               List<Result> results,
                               String errorMessage) implements ImdbDtoObject {

        public String getErrorMessage(){
            return errorMessage;
        }
    }

    private record Result(String id,
                          String title,
                          String resultType,
                          String description) {}

}
