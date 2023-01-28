package wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookDto;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LubimyCzytacService {

    private final WebOperations webOperations;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<BookDto> createBookDtoFromUrl(URL lubimyCzytacBookUrl) {
        log.info("Trying to parse book information from {}", lubimyCzytacBookUrl);
        Document parsedUrl = webOperations.parseUrl(lubimyCzytacBookUrl);
        Element dataElement = parsedUrl.getElementsByAttributeValue("type", "application/ld+json")
                .first();
        String data = dataElement != null ? dataElement.data() : "Failed to find book data";
        BookDto bookDto;
        try {
            bookDto = objectMapper.readValue(data, BookDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to map objet to BookDto because `{}` from data: {}", e.getMessage(), data);
            return Optional.empty();
        }
        String description = parseDescription(parsedUrl);
        bookDto.setDescription(description);
        String publisher = parsePublisher(parsedUrl);
        bookDto.setPublisher(publisher);
        bookDto.setUrl(lubimyCzytacBookUrl.toString());
        log.info("Created BookDto: {}", bookDto);
        return Optional.of(bookDto);
    }

    private String parsePublisher(Document parsedUrl) {
        Element publisherElement = parsedUrl.getElementsByAttributeValueContaining("href", "wydawnictwo")
                .first();
        return publisherElement != null ? publisherElement.text() : "Nieznany wydawca";
    }

    private String parseDescription(Document parsedUrl) {
        String description;
        try {
            description = parsedUrl.getElementsByAttributeValue("id", "book-description")
                    .first()
                    .getElementsByTag("p")
                    .first()
                    .text();
        } catch (NullPointerException ignored) {
            log.warn("Could not find description for this document");
            return "Ta książka nie ma opisu..";
        }
        return description;
    }

}
