package wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.util.SeriesConverter;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LubimyCzytacService {

    private final LubimyCzytacConfiguration configuration;
    private final SeriesConverter seriesConverter;
    private final WebOperations webOperations;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<BookDto> createBookDtoFromUrl(URL lubimyCzytacBookUrl) {
        log.info("Trying to parse book information from {}", lubimyCzytacBookUrl);
        Document parsedUrl = webOperations.parseUrl(lubimyCzytacBookUrl);
        Map<String, String> parsing = configuration.getParsing();
        Element dataElement = parsedUrl.getElementsByAttributeValue(parsing.get("main-attribute"), parsing.get("main-attribute-value"))
                .first();
        String data = dataElement != null ? dataElement.data() : "Failed to find book data";
        BookDto bookDto;
        try {
            bookDto = objectMapper.readValue(data, BookDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to map objet to BookDto because `{}` from data: {}", e.getMessage(), data);
            return Optional.empty();
        }
        bookDto.setUrl(lubimyCzytacBookUrl.toString());
        String description = parseDescription(parsedUrl);
        bookDto.setDescription(description);
        String publisher = parsePublisher(parsedUrl);
        bookDto.setPublisher(publisher);
        String series = parseSeries(parsedUrl);
        bookDto.setSeries(seriesConverter.convertToEntityAttribute(series));
        log.info("Created BookDto: {}", bookDto);
        return Optional.of(bookDto);
    }

    private String parseSeries(Document parsedUrl) {
        Map<String, String> parsing = configuration.getParsing();
        Element seriesElement = parsedUrl.getElementsByAttributeValueContaining(parsing.get("href"), parsing.get("series"))
                .first();
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
