package wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.FailsafeException;
import dev.failsafe.RetryPolicy;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.AuthorDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Series;
import wrzesniak.rafal.my.multimedia.manager.util.SeriesDynamoConverter;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Profile("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class LubimyCzytacServiceTest {

    @Autowired
    private LubimyCzytacConfiguration configuration;
    @Autowired
    private RetryPolicy<Object> retryPolicy;
    @Autowired
    private SeriesDynamoConverter seriesConverter;
    @Autowired
    private WebOperations webOperations;
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private LubimyCzytacService lubimyCzytacService;

    private static final URL HYPERION_URL = toURL("https://lubimyczytac.pl/ksiazka/231216/hyperion");
    private static final URL HIGH_END_URL = toURL("https://lubimyczytac.pl/ksiazka/5056478/high-end-dlaczego-potrzebujemy-doskonalosci");
    private static final BookDto HYPERION_DTO = buildHyperionDto();
    private static final BookDto HIGH_END_DTO = buildHighEndDto();


    @Test
    public void shouldCreateHyperionBookProperly() {
        // when
        Optional<BookDto> bookDtoFromUrl = lubimyCzytacService.createBookDtoFromUrl(HYPERION_URL);

        // then
        assertTrue(bookDtoFromUrl.isPresent());
        assertEquals(HYPERION_DTO, bookDtoFromUrl.get());
    }

    @Test
    public void shouldCreateHighEndBookProperly() {
        // when
        Optional<BookDto> bookDtoFromUrl = lubimyCzytacService.createBookDtoFromUrl(HIGH_END_URL);

        // then
        assertTrue(bookDtoFromUrl.isPresent());
        assertEquals(HIGH_END_DTO, bookDtoFromUrl.get());
    }

    @Test
    public void shouldThrowWhenCannotParse() {
        assertThrows(FailsafeException.class, () -> lubimyCzytacService.createBookDtoFromUrl(toURL("https://lubimyczytac.pl/ksiazka/1234/bad-url")));
    }

    @Test
    public void shouldThrowWhenNotValidUrl() {
        assertThrows(ConstraintViolationException.class, () -> lubimyCzytacService.createBookDtoFromUrl(toURL("https://bad-domain-url.pl/ksiazka/1234/bad-url")), "Invalid lubimy czytac url");
    }

    private static BookDto buildHyperionDto() {
        return BookDto.builder()
                .name("Hyperion")
                .isbn("9788374805575")
                .datePublished(LocalDate.of(1990, 1, 1))
                .genre("https://lubimyczytac.pl/ksiazki/k/41/fantasy-science-fiction")
                .numberOfPages(518)
                .image("https://s.lubimyczytac.pl/upload/books/231000/231216/395509-352x500.jpg")
                .author(new AuthorDto("Dan Simmons"))
                .description("W obliczu zbliżającej się nieuchronnie międzygalaktycznej wojny na planetę Hyperion przybywa siedmioro pielgrzymów: Kapłan, Żołnierz, Uczony, Poeta, Kapitan, Detektyw i Konsul. Mają za zadanie dotrzeć do mitycznych grobowców, by znaleźć w nich budzącą grozę istotę. Zna ona być może metodę, która pozwoli zapobiec zagładzie całej ludzkości. Każdy z pielgrzymów może przedstawić jej swoją prośbę, lecz wysłuchany zostanie tylko jeden. Pozostali będą musieli zginąć.")
                .publisher("Mag")
                .url("https://lubimyczytac.pl/ksiazka/231216/hyperion")
                .series(new Series("Hyperion", 1))
                .build();
    }

    private static BookDto buildHighEndDto() {
        return BookDto.builder()
                .name("High-end. Dlaczego potrzebujemy doskonałości")
                .isbn("9788324088201")
                .datePublished(LocalDate.of(2023, 3, 13))
                .genre("https://lubimyczytac.pl/ksiazki/k/46/reportaz")
                .numberOfPages(336)
                .image("https://s.lubimyczytac.pl/upload/books/5056000/5056478/1048530-352x500.jpg")
                .author(new AuthorDto("Bartosz Pacuła"))
                .description("Mniej znaczy więcej. Ludzka kreatywność nie zna granic. Wynalazki i wprowadzane innowacje technologiczne mają oszczędzać nasz czas. Na całym świecie trwa wyścig o tempo i obniżanie kosztów produkcji. Nie ma znaczenia, czy jest to pieczywo, bawełniana koszula czy samochód. Ludzi zastępują roboty i maszyny, które nie popełniają błędów. Szybciej, więcej, taniej. Stop! Zwolnijmy i zróbmy krótki rachunek sumienia. Ile z tych przedmiotów zepsuło się i nie można już ich naprawić? Jaki koszt przy takiej eksploatacji zasobów ponosi środowisko? Czy obcowanie z takimi rzeczami sprawia nam przyjemność? W tej nieustannej pogoni zapominamy, że nic nie zastąpi pracy ludzkich rąk. Wysoka jakość i trwałość wymagają rzemieślniczej dokładności. Poznajmy ludzi i wejdźmy do ich warsztatu, w którym nadają rzeczom „duszę”")
                .publisher("Znak Horyzont")
                .url("https://lubimyczytac.pl/ksiazka/5056478/high-end-dlaczego-potrzebujemy-doskonalosci")
                .build();
    }
}