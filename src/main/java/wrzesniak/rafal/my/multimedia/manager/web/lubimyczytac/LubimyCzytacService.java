package wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac;

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
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.lubimyczytac.LubimyCzytacUrl;
import wrzesniak.rafal.my.multimedia.manager.util.SeriesDynamoConverter;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class LubimyCzytacService {

    private final LubimyCzytacConfiguration configuration;
    private final RetryPolicy<Object> retryPolicy;
    private final SeriesDynamoConverter seriesConverter;
    private final WebOperations webOperations;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<BookDto> createBookDtoFromUrl(@LubimyCzytacUrl URL lubimyCzytacBookUrl) {
        log.info("Trying to parse book information from {}", lubimyCzytacBookUrl);
        AtomicReference<Document> parsedUrlAtomic = new AtomicReference<>();
        Failsafe.with(retryPolicy)
                .run(() -> parsedUrlAtomic.set(webOperations.parseUrl(lubimyCzytacBookUrl)));

        Document parsedUrl = parsedUrlAtomic.get();
        Map<String, String> parsing = configuration.getParsing();
        Element dataElement = parsedUrl.getElementsByAttributeValue(parsing.get("main-attribute"), parsing.get("main-attribute-value")).get(1);
        String data = dataElement != null ? dataElement.data() : "Failed to find book data";
        BookDto bookDto;
        try {
            bookDto = objectMapper.readValue(data, BookDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to map object to BookDto because `{}` from data: {}", e.getMessage(), data);
            return Optional.empty();
        }
        BookDto enrichedDto = enrichDtoWithAdditionalData(bookDto, lubimyCzytacBookUrl, parsedUrl);
        log.info("Created BookDto: {}", enrichedDto);
        return Optional.of(enrichedDto);
    }

    private BookDto enrichDtoWithAdditionalData(BookDto bookDto, URL lubimyCzytacBookUrl, Document parsedUrl) {
        bookDto.setUrl(lubimyCzytacBookUrl.toString());
        bookDto.setDescription(parseDescription(parsedUrl));
        bookDto.setPublisher(parsePublisher(parsedUrl));
        parseOriginalDatePublished(parsedUrl).ifPresent(bookDto::setDatePublished);
        String series = parseSeries(parsedUrl);
        bookDto.setSeries(seriesConverter.transformTo(AttributeValue.fromS(series)));
        return bookDto;
    }

    private Optional<LocalDate> parseOriginalDatePublished(Document parsedUrl) {
        Map<String, String> parsing = configuration.getParsing();
        Optional<Element> originalReleaseText = Optional.ofNullable(parsedUrl.getElementsByAttributeValue(parsing.get("title"), parsing.get("original-release")).first());
        Optional<Elements> elementsWithOriginalDate = originalReleaseText.map(element -> parsedUrl.getElementsByIndexEquals(element.elementSiblingIndex() + 1));
        return elementsWithOriginalDate
                .flatMap(elements -> elements.stream()
                    .map(Element::text)
                    .map(this::tryMappingToLocalDate)
                    .filter(Objects::nonNull)
                    .findFirst());
    }

    private LocalDate tryMappingToLocalDate(String s) {
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String parseSeries(Document parsedUrl) {
        Map<String, String> parsing = configuration.getParsing();
        Element seriesElement = parsedUrl.getElementsByAttributeValueContaining(parsing.get("href"), parsing.get("series")).first();
        return seriesElement != null ? seriesElement.text() : null;
    }

    private String parsePublisher(Document parsedUrl) {
        Map<String, String> parsing = configuration.getParsing();
        Element publisherElement = parsedUrl.getElementsByAttributeValueContaining(parsing.get("href"), parsing.get("publisher"))
                .first();
        return publisherElement != null ? publisherElement.text() : configuration.getUnknownPublisher();
    }

    private String parseDescription(Document parsedUrl) {
        Map<String, String> parsing = configuration.getParsing();
        String description;
        try {
            description = parsedUrl.getElementsByAttributeValue(parsing.get("id"), parsing.get("description"))
                    .first()
                    .getElementsByTag("p")
                    .first()
                    .text();
        } catch (NullPointerException ignored) {
            log.warn("Could not find description for this document");
            return configuration.getDefaultDescription();
        }
        return description;
    }

}
