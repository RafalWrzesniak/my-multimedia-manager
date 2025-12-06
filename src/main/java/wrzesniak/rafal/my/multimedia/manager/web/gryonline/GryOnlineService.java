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
            gameDto = objectMapper.readValue(data, GameDtoWrapper.class).about();
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
        return Optional.ofNullable(parsedUrl.getElementsByClass("S016-box-img-c").first())
                .map(element -> element.getElementsByTag("img").first())
                .map(element -> toURL(element.attr("src")))
                .orElse(null);
    }

    private LocalDate getDateReleaseDateForPlatform(Document parsedUrl, GamePlatform platform) {
        Elements premiereElements = parsedUrl.getElementsByClass("S016meta-plat-box2");
        Element platformReleaseDateElement = getReleaseDateElementForPlatform(premiereElements, platform);
        Optional<String> stringDate = buildStringDateFromElement(platformReleaseDateElement);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.of("pl"));
        return stringDate
                .map(dateString -> dateString.contains(" za ") ? dateString.substring(0, dateString.indexOf(" za ")) : dateString)
                .map(dateString -> LocalDate.parse(dateString, formatter))
                .orElse(null);
    }

    private Optional<String> buildStringDateFromElement(Element element) {
        return Optional.ofNullable(element)
                .map(el -> el.getElementsByClass("S016meta-s-p5").first())
                .map(Element::text);
    }

    private Element getReleaseDateElementForPlatform(Elements premiereElements, GamePlatform platform) {
        if(platform == null) {
            return premiereElements.first();
        }
        return premiereElements.stream()
                .filter(element -> {
                    Element element1 = element.getElementsByTag("p").first();
                    return element1 != null && element1.text().contains(platform.name());
                })
                .findFirst()
                .orElse(premiereElements.first());
    }

    private record GameDtoWrapper(GameDto about) {}

}
