package wrzesniak.rafal.my.multimedia.manager.web.gryonline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GamePlatform;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GryOnlineService {

    private final ObjectMapper objectMapper;
    private final WebOperations webOperations;

    @SneakyThrows
    public Optional<GameDto> createGameDtoFromUrl(URL gryOnlineUrl, GamePlatform platform) {
        log.info("Trying to parse game information from {}", gryOnlineUrl);
        Document parsedUrl = webOperations.parseUrl(gryOnlineUrl);
        Element dataElement = parsedUrl.getElementsByAttributeValue("type", "application/ld+json")
                .first();
        String data = dataElement != null ? dataElement.data() : "Failed to find game data";
        GameDto gameDto;
        try {
            gameDto = objectMapper.readValue(data, GameDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to map objet to GameDto because `{}` from data: {}", e.getMessage(), data);
            return Optional.empty();
        }
        LocalDate releaseDate = getDateReleaseDateForPlatform(parsedUrl, platform);
        gameDto.setReleaseDate(releaseDate);
        log.info("Created GameDto: {}", gameDto);
        return Optional.of(gameDto);
    }

    private LocalDate getDateReleaseDateForPlatform(Document parsedUrl, GamePlatform platform) {
        Elements premiereElements = parsedUrl.getElementsByAttributeValue("class", "multi-p");
        Element platformReleaseDateElement = getReleaseDateElementForPlatform(premiereElements, platform);
        Optional<String> stringDate = buildStringDateFromElement(platformReleaseDateElement);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d][dd] [MMM] yyyy", new Locale("pl"));
        return stringDate.map(dateString -> LocalDate.parse(dateString, formatter)).orElse(null);
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
