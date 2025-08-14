package wrzesniak.rafal.my.multimedia.manager.web.gryonline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@Service
@RequiredArgsConstructor
public class GryOnlineService {

    private final ObjectMapper objectMapper;
    private final WebOperations webOperations;
    private final RetryPolicy<Object> retryPolicy;

    @SneakyThrows
    public Optional<GameDto> createGameDtoFromUrl(URL gryOnlineUrl, GamePlatform platform) {
        log.info("Trying to parse game information from {}", gryOnlineUrl);
        AtomicReference<Document> parsedUrl = new AtomicReference<>();
        Failsafe.with(retryPolicy)
                .run(() -> parsedUrl.set(webOperations.parseUrl(gryOnlineUrl)));
        Element dataElement = parsedUrl.get().getElementsByAttributeValue("type", "application/ld+json")
                .first();
        String data = Optional.ofNullable(dataElement)
                .map(Element::data)
                .orElse(null);
        GameDto gameDto;
        try {
            gameDto = objectMapper.readValue(data, GameDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to map object to GameDto because `{}` from data: {}", e.getMessage(), data);
            return Optional.empty();
        }
        LocalDate releaseDate = getDateReleaseDateForPlatform(parsedUrl.get(), platform);
        gameDto.setReleaseDate(releaseDate);
        Optional.ofNullable(getGameImageUrl(parsedUrl.get())).ifPresent(gameDto::setImage);
        log.info("Created GameDto: {}", gameDto);
        return Optional.of(gameDto);
    }

    private URL getGameImageUrl(Document parsedUrl) {
        return Optional.ofNullable(parsedUrl.getElementById("game-cover-src"))
                .map(element -> toURL(element.attr("src")))
                .orElse(null);
    }

    private LocalDate getDateReleaseDateForPlatform(Document parsedUrl, GamePlatform platform) {
        Elements premiereElements = parsedUrl.getElementsByAttributeValue("class", "multi-p");
        Element platformReleaseDateElement = getReleaseDateElementForPlatform(premiereElements, platform);
        Optional<String> stringDate = buildStringDateFromElement(platformReleaseDateElement);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd] [MMM] yyyy", new Locale("pl"));
        return stringDate
                .map(dateString -> dateString.contains(" za ") ? dateString.substring(0, dateString.indexOf(" za ")) : dateString)
                .map(dateString -> LocalDate.parse(dateString, formatter))
                .orElse(null);
    }

    private Optional<String> buildStringDateFromElement(Element element) {
        return Optional.ofNullable(element).flatMap(e -> e.getElementsByTag("span").stream()
                .map(el -> el.attr("class").equals("s2") ? el.text().substring(0, 3) : el.text())
                .reduce((s1, s2) -> s1 + " " + s2));
    }

    private Element getReleaseDateElementForPlatform(Elements premiereElements, GamePlatform platform) {
        return premiereElements.stream()
                .filter(element -> element.getElementsByTag("a").stream().
                        anyMatch(element1 -> platform == null || element1.text().equals(platform.name())))
                .findFirst()
                .orElse(premiereElements.first());
    }

}
