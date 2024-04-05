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
                .image("https://s.lubimyczytac.pl/upload/books/231000/231216/1135772-352x500.jpg")
                .author(new AuthorDto("Dan Simmons"))
                .description("\"Hyperion\" to wciągająca i bestsellerowa powieść science-fiction. O odległych galaktykach, końcu Ziemi i niezwykłej misji, jaką wykonać musi siedmioro niezwyciężonych pielgrzymów. Powieść, którą pokochały miliony Czytelników na całym świecie z wciągającą niebanalną fabułą, a co najważniejsze wieloma wątkami, zgrabnie łączącymi się w logiczną całość. \"Hyperion\", czyli książka, którą napisał Dan Simmons przenosi nas w niezwykły świat science-fiction. Stara i dobrze znana Ziemia umiera, przez co ludzkość zostaje umieszczona na odległych od siebie innych planetach. Nie jest jednak oczywiste, że od teraz wszyscy będą żyć ze sobą w zgodzie. Zbliża się międzygalaktyczna wojna, dlatego też na planetę Hyperion przybywa siedmioro pielgrzymów. Są to Kapłan, Żołnierz, Uczony, Poeta, Kapitan, Detektyw i Konsul. Niezwyciężeni, silni i zdeterminowani, muszą dotrzeć do mitycznych grobowców i znaleźć istotę, która budzi grozę. Możliwe, że tylko ona zna metodę, która mogłaby pomóc zapobiec zagładzie ludzkości. Każdy z przybyłych może przedstawić swoją prośbę, chociaż wysłuchana zostanie wyłącznie jedna. Pozostali będą musieli zginąć. \"Hyperion\" to wciągająca powieść science-fiction. Znany autor pokazuje nam swój kunszt literacki, dzięki któremu trudno nam będzie oderwać się od lektury. Bohaterowie są wyraziście wykreowanymi postaciami, pełnymi sprzeczności i wątpliwości. Nie wahają się jednak przed tym, aby uratować ludzkość od zagłady i sprawić, że możliwy będzie dalszy pokój. Tylko czy będą w stanie wypełnić swoją misję? Czy tajemnicza i przerażająca istota przystanie na ich propozycje i również uzna, że dobro większości jest ważniejsze niż własne?")
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