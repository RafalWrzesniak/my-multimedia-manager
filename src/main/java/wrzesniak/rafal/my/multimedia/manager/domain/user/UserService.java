package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoListWithSuchIdException;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoSuchUserException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final GameFacade gameFacade;
    private final BookFacade bookFacade;
    private final MovieFacade movieFacade;
    private final ContentListDynamoService contentListDynamoService;
    private final DynamoDbClientGeneric<UserDynamo> userDynamoDb;

    public UserDynamo createNewUser(String username, String preferredUsername, String email) {
        UserDynamo user = new UserDynamo(username, preferredUsername, email);
        userDynamoDb.saveItem(user);
        createAllContentListForNewUser(username);
        log.info("New user created successfully: {}, {}", username, preferredUsername);
        return user;
    }

    public void setSynchronizationInfo(String username, SyncInfo syncInfo) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        userDynamo.addNewSynchronization(syncInfo);
        userDynamoDb.saveItem(userDynamo);
    }

    public UserLists fetchNeededListData(String username, SyncInfoWrapper syncInfoWrapper) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        log.info("Starts fetching basic list info for {}", userDynamo.getPreferredUsername());
        markUserLoggedIn(userDynamo.getUsername());
        List<ContentListDynamo> rawLists;
        if(syncFromUiUpToDateOrNewerThanLatestOnServer(syncInfoWrapper.syncInfo(), userDynamo)) {
            log.info("UI has latest data, returning list from UI");
            return syncInfoWrapper.currentLists();
        }
        if(noSyncOnServer(userDynamo) && requestContainsLists(syncInfoWrapper)) {
            log.info("So sync data on server, returning list from UI");
            return syncInfoWrapper.currentLists();
        }
        if(lastSynchronizationContains(userDynamo, syncInfoWrapper.syncInfo()) && requestContainsLists(syncInfoWrapper)) {
            rawLists = fetchOnlyMissingLists(syncInfoWrapper, userDynamo);
        } else {
            rawLists = fetchAllContentLists(userDynamo.getUsername());
        }
        log.info("Enriching remaining lists with user details");
        UserLists enrichedLists = enrichRawListsWithUserDetails(rawLists, userDynamo.getUsername());
        log.info("Merging lists");
        UserLists mergedLists = mergeEnrichedListsWithGivenOnes(syncInfoWrapper, enrichedLists);
        log.info("Returning {} lists", mergedLists.getAllLists().size());
        return mergedLists;
    }

    private boolean syncFromUiUpToDateOrNewerThanLatestOnServer(SyncInfo syncInfo, UserDynamo userDynamo) {
        LocalDateTime uiSyncTimestamp = syncInfo.syncTimestamp();
        if(uiSyncTimestamp == null) {
            return false;
        }
        if(noSyncOnServer(userDynamo)) {
            return true;
        }
        LocalDateTime lastServerTimestamp = userDynamo.getLastSynchronization().getFirst().syncTimestamp();
        return uiSyncTimestamp.isAfter(lastServerTimestamp) || uiSyncTimestamp.equals(lastServerTimestamp);
    }

    private boolean requestContainsLists(SyncInfoWrapper syncInfoWrapper) {
        return syncInfoWrapper.currentLists() != null && !syncInfoWrapper.currentLists().isEmpty();
    }

    private boolean noSyncOnServer(UserDynamo userDynamo) {
        return userDynamo.getLastSynchronization().isEmpty();
    }

    private List<ContentListDynamo> fetchOnlyMissingLists(SyncInfoWrapper syncInfoWrapper, UserDynamo userDynamo) {
        List<ContentListDynamo> rawLists;
        List<String> listIds = getNotSynchronizedListsSince(userDynamo, syncInfoWrapper.syncInfo());
        log.info("Found {} lists to synchronize", listIds.size());
        rawLists = listIds.stream()
                .map(listId -> getListIfExists(listId, userDynamo.getUsername()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return rawLists;
    }

    private Optional<ContentListDynamo> getListIfExists(String listId, String username) {
        try {
            return Optional.of(contentListDynamoService.getListById(listId, username));
        } catch (NoListWithSuchIdException e) {
            return Optional.empty();
        }
    }

    private List<ContentListDynamo> fetchAllContentLists(String username) {
        log.info("Fetching every list");
        List<ContentListDynamo> rawLists = contentListDynamoService.getAllContentLists(username);
        if(rawLists.isEmpty()) {
            rawLists = createAllContentListForNewUser(username);
        }
        return rawLists;
    }

    private List<ContentListDynamo> createAllContentListForNewUser(String username) {
        List<ContentListDynamo> lists = Arrays.stream(ContentListType.values())
                .map(contentListType -> contentListDynamoService.createContentList(contentListType.getAllProductsListName(),
                        username, contentListType, true))
                .toList();
        log.info("New users list created successfully: {}", username);
        return lists;
    }

    private UserLists mergeEnrichedListsWithGivenOnes(SyncInfoWrapper syncInfoWrapper, UserLists fetchedLists) {
        if(syncInfoWrapper.currentLists() == null || syncInfoWrapper.currentLists().isEmpty()) {
            return fetchedLists;
        }
        syncInfoWrapper.currentLists().getBookLists().stream()
                .filter(list -> !fetchedLists.contains(list))
                .filter(list -> !syncInfoWrapper.syncInfo().changedListIds().contains(list.getId()))
                .forEach(fetchedLists::add);
          syncInfoWrapper.currentLists().getMovieLists().stream()
                .filter(list -> !fetchedLists.contains(list))
                .filter(list -> !syncInfoWrapper.syncInfo().changedListIds().contains(list.getId()))
                .forEach(fetchedLists::add);
          syncInfoWrapper.currentLists().getGameLists().stream()
                .filter(list -> !fetchedLists.contains(list))
                .filter(list -> !syncInfoWrapper.syncInfo().changedListIds().contains(list.getId()))
                .forEach(fetchedLists::add);
        return fetchedLists;
    }

    private UserLists enrichRawListsWithUserDetails(List<ContentListDynamo> rawLists, String username) {
        Callable<List<BookListWithUserDetails>> fetchBooks = () -> getListWithUserDetails(rawLists, username, BOOK_LIST,
                bookFacade::getListWithEnrichedProductsForPageSize, BookListWithUserDetails::getName);
        Callable<List<MovieListWithUserDetails>> fetchMovies = () -> getListWithUserDetails(rawLists, username, MOVIE_LIST,
                movieFacade::getListWithEnrichedProductsForPageSize, MovieListWithUserDetails::getName);
        Callable<List<GameListWithUserDetails>> fetchGames = () -> getListWithUserDetails(rawLists, username, GAME_LIST,
                gameFacade::getListWithEnrichedProductsForPageSize, GameListWithUserDetails::getName);

        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
            Future<List<BookListWithUserDetails>> booksFetcher = executor.submit(fetchBooks);
            Future<List<MovieListWithUserDetails>> moviesFetcher = executor.submit(fetchMovies);
            Future<List<GameListWithUserDetails>> gamesFetcher = executor.submit(fetchGames);

            List<BookListWithUserDetails> bookLists = booksFetcher.get();
            List<MovieListWithUserDetails> movieLists = moviesFetcher.get();
            List<GameListWithUserDetails> gameLists = gamesFetcher.get();
            return new UserLists(bookLists, movieLists, gameLists);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> getListWithUserDetails(List<ContentListDynamo> rawLists, String username, ContentListType listType,
                                               BiFunction<ContentListDynamo, String, T> getList,
                                               Function<T, String> sorting) {
        return rawLists.parallelStream()
                .filter(contentListDynamo -> listType.equals(contentListDynamo.getContentListType()))
                .map(contentListDynamo -> getList.apply(contentListDynamo, username))
                .sorted(Comparator.comparing(sorting))
                .toList();
    }

    private List<String> getNotSynchronizedListsSince(UserDynamo userDynamo, SyncInfo syncInfo) {
        List<SyncInfo> lastSynchronizations = userDynamo.getLastSynchronization();
        if(!lastSynchronizationContains(userDynamo, syncInfo)) {
            log.warn("Sync {} not found in users: {}", syncInfo, lastSynchronizations);
            throw new IllegalStateException("Sync not found in user");
        }
        return lastSynchronizations.stream()
                .filter(sync -> sync.syncTimestamp().isAfter(syncInfo.syncTimestamp()))
                .map(SyncInfo::changedListIds)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    private boolean lastSynchronizationContains(UserDynamo userDynamo, SyncInfo syncInfo) {
        if(syncInfo == null || syncInfo.syncTimestamp() == null) {
            return false;
        }
        LocalDateTime first = userDynamo.getLastSynchronization().getFirst().syncTimestamp();
        LocalDateTime last = userDynamo.getLastSynchronization().getLast().syncTimestamp();
        return last.isBefore(syncInfo.syncTimestamp()) && first.isAfter(syncInfo.syncTimestamp());
    }


    private void markUserLoggedIn(String username) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        userDynamo.markedLoggedIn();
        userDynamoDb.saveItem(userDynamo);
    }

}
