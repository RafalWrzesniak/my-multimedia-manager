package wrzesniak.rafal.my.multimedia.manager.web.gryonline;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.RetryPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto.Author;
import wrzesniak.rafal.my.multimedia.manager.web.WebOperations;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto.Publisher;
import static wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto.builder;
import static wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform.*;
import static wrzesniak.rafal.my.multimedia.manager.domain.game.objects.PlayMode.MultiPlayer;
import static wrzesniak.rafal.my.multimedia.manager.domain.game.objects.PlayMode.SinglePlayer;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Profile("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class GryOnlineServiceTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebOperations webOperations;
    @Autowired
    private RetryPolicy<Object> retryPolicy;

    @Autowired
    private GryOnlineService gryOnlineService;

    private static final URL CSGO_GAME_URL = toURL("https://www.gry-online.pl/gry/counter-strike-global-offensive/z02d7c");
    private static final URL INDIANA_JONES_GAME_URL = toURL("https://www.gry-online.pl/gry/indiana-jones-and-the-great-circle/z15df2");

    private static final GameDto CSGO_DTO = buildCsgoDto();

    private static final GameDto INDIANA_JONES_DTO = buildIndianaJonesDto();

    @Test
    public void shouldCreateCsgoEntryFromUrl() {
        // when
        Optional<GameDto> gameDtoFromUrl = gryOnlineService.createGameDtoFromUrl(CSGO_GAME_URL, PC);

        // then
        assertEquals(CSGO_DTO, gameDtoFromUrl.get());
    }

    @Test
    public void shouldCreateIndianaJonesEntryFromUrl() {
        // when
        Optional<GameDto> gameDtoFromUrl = gryOnlineService.createGameDtoFromUrl(INDIANA_JONES_GAME_URL, PC);

        // then
        assertEquals(INDIANA_JONES_DTO, gameDtoFromUrl.get());
    }

    private static GameDto buildCsgoDto() {
        return builder()
                .name("Counter-Strike: Global Offensive")
                .url(CSGO_GAME_URL)
                .image(toURL("https://cdn.gracza.pl/galeria/gry13/grupy/11644.jpg"))
                .description("Nowa odsłona legendarnej sieciowej strzelaniny, która zadebiutowała blisko dekadę wcześniej jako modyfikacja pierwszej części FPS-a Half-Life. Za jej powstanie odpowiada sama firma Valve Corporation, która, korzystając z popularności moda, wykupiła prawa do jego rozwijania.")
                .publisher(new Publisher("Valve Corporation"))
                .genre(List.of("Akcji"))
                .playMode(List.of(SinglePlayer, MultiPlayer))
                .gamePlatform(List.of(PC, XBOX_360, PS3))
                .author(new Author("Valve Corporation"))
                .releaseDate(LocalDate.of(2012, 8, 21))
                .build();
    }

    private static GameDto buildIndianaJonesDto() {
        return builder()
                .name("Indiana Jones i Wielki Krąg")
                .url(INDIANA_JONES_GAME_URL)
                .image(toURL("https://cdn.gracza.pl/galeria/gry13/grupy/90185656.jpg"))
                .description("Przygodowa gra akcji FPP od studia MachineGames (znanego z serii Wolfenstein). Indiana Jones and the Great Circle opowiada oryginalną historię o przygodach tytułowego archeologa (nieopartą na żadnym z filmów), któremu wizerunku użyczył Harrison Ford.")
                .publisher(new Publisher("Bethesda Softworks"))
                .genre(List.of("Akcji"))
                .playMode(List.of(SinglePlayer))
                .gamePlatform(List.of(PC, XSX, PS5, Switch2))
                .author(new Author("Machine Games"))
                .releaseDate(LocalDate.of(2024, 12, 9))
                .build();
    }
}