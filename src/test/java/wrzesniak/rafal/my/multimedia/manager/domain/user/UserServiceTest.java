package wrzesniak.rafal.my.multimedia.manager.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.ListDto;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieFacade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private ContentListDynamoService contentListDynamoService;

    @Mock
    private DynamoDbClientGeneric<UserDynamo> userDynamoDb;

    @Mock
    private BookFacade bookFacade;
    @Mock
    private MovieFacade movieFacade;
    @Mock
    private GameFacade gameFacade;

    @InjectMocks
    private UserService userService;

    private static final String USERNAME = "Username";
    private static final UserDynamo USER_DYNAMO = new UserDynamo(USERNAME, USERNAME, "mail");
    private static final ContentListDynamo EMPTY_BOOK_LIST = new ContentListDynamo("Wszystkie książki", USERNAME, BOOK_LIST, true);
    private static final ContentListDynamo EMPTY_MOVIE_LIST = new ContentListDynamo("Wszystkie filmy", USERNAME, MOVIE_LIST, true);
    private static final ContentListDynamo EMPTY_GAME_LIST = new ContentListDynamo("Wszystkie gry", USERNAME, GAME_LIST, true);

    @Test
    void shouldFetchAllDataWhenSyncInfoInRequestIsEmpty() {
        // given
        SyncInfo lastUiSyncInfo = new SyncInfo(null, null);
        USER_DYNAMO.setLastSynchronization(List.of(new SyncInfo(LocalDateTime.now().minusDays(1), List.of("id1"))));
        when(userDynamoDb.getItemById(USERNAME)).thenReturn(Optional.of(USER_DYNAMO));
        when(contentListDynamoService.getAllContentLists(USERNAME)).thenReturn(List.of(EMPTY_BOOK_LIST));
        // then
        userService.fetchNeededListData(USERNAME, new SyncInfoWrapper(lastUiSyncInfo, null));

        // then
        verify(contentListDynamoService, times(1)).getAllContentLists(anyString());
        verifyNoMoreInteractions(contentListDynamoService);
    }

    @Test
    void shouldFetchAllDataWhenSyncInfoInRequestIsNull() {
        // given
        USER_DYNAMO.setLastSynchronization(List.of(new SyncInfo(LocalDateTime.now().minusDays(1), List.of("id1"))));
        when(userDynamoDb.getItemById(USERNAME)).thenReturn(Optional.of(USER_DYNAMO));
        when(contentListDynamoService.getAllContentLists(USERNAME)).thenReturn(List.of(EMPTY_BOOK_LIST));
        // then
        userService.fetchNeededListData(USERNAME, new SyncInfoWrapper(null, null));

        // then
        verify(contentListDynamoService, times(1)).getAllContentLists(anyString());
        verifyNoMoreInteractions(contentListDynamoService);
    }

    @Test
    void shouldCreateNewListsWhenNoAvailableForUser() {
        // given
        SyncInfo lastUiSyncInfo = new SyncInfo(null, null);
        USER_DYNAMO.setLastSynchronization(List.of(new SyncInfo(LocalDateTime.now().minusDays(1), List.of("id1"))));
        when(userDynamoDb.getItemById(USERNAME)).thenReturn(Optional.of(USER_DYNAMO));
        when(contentListDynamoService.getAllContentLists(USERNAME)).thenReturn(List.of());
        when(contentListDynamoService.createContentList(BOOK_LIST.getAllProductsListName(), USERNAME, BOOK_LIST, true)).thenReturn(EMPTY_BOOK_LIST);
        when(contentListDynamoService.createContentList(MOVIE_LIST.getAllProductsListName(), USERNAME, MOVIE_LIST, true)).thenReturn(EMPTY_MOVIE_LIST);
        when(contentListDynamoService.createContentList(GAME_LIST.getAllProductsListName(), USERNAME, GAME_LIST, true)).thenReturn(EMPTY_GAME_LIST);

        // then
        userService.fetchNeededListData(USERNAME, new SyncInfoWrapper(lastUiSyncInfo, null));

        // then
        verify(contentListDynamoService, times(1)).getAllContentLists(anyString());
        verify(contentListDynamoService, times(3)).createContentList(anyString(), eq(USERNAME), any(ContentListType.class), eq(true));
        verifyNoMoreInteractions(contentListDynamoService);
    }

    @Test
    void shouldFetchAllDataWhenSyncInfoInRequestPresentButItIsNotInDb() {
        // given
        USER_DYNAMO.setLastSynchronization(List.of());
        when(userDynamoDb.getItemById(USERNAME)).thenReturn(Optional.of(USER_DYNAMO));
        when(contentListDynamoService.getAllContentLists(USERNAME)).thenReturn(List.of(EMPTY_BOOK_LIST));
        // then
        userService.fetchNeededListData(USERNAME, new SyncInfoWrapper(new SyncInfo(LocalDateTime.now(), List.of("id1")), null));

        // then
        verify(contentListDynamoService, times(1)).getAllContentLists(anyString());
        verifyNoMoreInteractions(contentListDynamoService);
    }

    @Test
    void shouldFetchAllListsWhenNoCurrentListsInRequest() {
        // given
        LocalDateTime lastUiSyncDateTime = LocalDateTime.of(2024, 7, 5, 16, 0);
        SyncInfo lastUiSyncInfo = new SyncInfo(lastUiSyncDateTime, List.of("id3"));
        USER_DYNAMO.setLastSynchronization(List.of(
                new SyncInfo(lastUiSyncDateTime.minusDays(3), List.of("id1", "id2")),
                new SyncInfo(lastUiSyncDateTime.minusDays(2), List.of("id3")),
                lastUiSyncInfo,
                new SyncInfo(lastUiSyncDateTime.plusDays(1), List.of("id4")),
                new SyncInfo(lastUiSyncDateTime.plusDays(2), List.of("id4", "id5"))
        ));
        when(userDynamoDb.getItemById(USERNAME)).thenReturn(Optional.of(USER_DYNAMO));
        when(contentListDynamoService.getAllContentLists(USERNAME)).thenReturn(List.of(EMPTY_BOOK_LIST));

        // when
        userService.fetchNeededListData(USERNAME, new SyncInfoWrapper(lastUiSyncInfo, null));

        // then
        verify(contentListDynamoService, times(1)).getAllContentLists(anyString());
        verifyNoMoreInteractions(contentListDynamoService);
    }

    @Test
    void shouldFetchOneRemainingList() {
        // given
        LocalDateTime lastUiSyncDateTime = LocalDateTime.of(2024, 7, 5, 16, 0);
        SyncInfo lastUiSyncInfo = new SyncInfo(lastUiSyncDateTime, List.of("id3"));
        USER_DYNAMO.setLastSynchronization(List.of(
                new SyncInfo(lastUiSyncDateTime.minusDays(3), List.of("id1", "id2")),
                new SyncInfo(lastUiSyncDateTime.minusDays(2), List.of("id3")),
                lastUiSyncInfo,
                new SyncInfo(lastUiSyncDateTime.plusDays(1), List.of("id4")),
                new SyncInfo(lastUiSyncDateTime.plusDays(2), List.of("id4", "id5"))
        ));
        when(userDynamoDb.getItemById(USERNAME)).thenReturn(Optional.of(USER_DYNAMO));
        when(contentListDynamoService.getListById("id4", USERNAME)).thenReturn(EMPTY_BOOK_LIST);
        when(contentListDynamoService.getListById("id5", USERNAME)).thenReturn(EMPTY_GAME_LIST);

        // when
        userService.fetchNeededListData(USERNAME, new SyncInfoWrapper(lastUiSyncInfo, List.of(
                ListDto.builder().id("id1").build(),
                ListDto.builder().id("id2").build(),
                ListDto.builder().id("id3").build(),
                ListDto.builder().id("id4").build(),
                ListDto.builder().id("id5").build())
        ));

        // then
        verify(contentListDynamoService, times(0)).getAllContentLists(anyString());
        verify(contentListDynamoService, times(1)).getListById("id4", USERNAME);
        verify(contentListDynamoService, times(1)).getListById("id5", USERNAME);
        verifyNoMoreInteractions(contentListDynamoService);
    }
}