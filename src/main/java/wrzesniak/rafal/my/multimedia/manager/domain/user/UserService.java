package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.ListDto;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.SimpleItemDtoWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.error.NoSuchUserException;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;

import java.util.*;

import static wrzesniak.rafal.my.multimedia.manager.controller.BaseProductController.PAGE_SIZE;

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

    public List<ListDto> fetchNeededListData(String username, SyncInfoWrapper syncInfoWrapper) {
        log.info("Starts fetching basic list info for {}", username);
        markUserLoggedIn(username);
        List<ContentListDynamo> rawLists;
        if(lastSynchronizationContains(username, syncInfoWrapper.syncInfo()) && (syncInfoWrapper.currentLists() != null && !syncInfoWrapper.currentLists().isEmpty())) {
            rawLists = fetchOnlyMissingLists(syncInfoWrapper, username);
        } else {
            rawLists = fetchAllContentLists(username);
        }
        List<ListDto> enrichedLists = enrichRawListsWithUserDetails(rawLists, username);
        log.info("Enriched remaining lists with user details");
        return mergeEnrichedListsWithGivenOnes(syncInfoWrapper, enrichedLists);
    }

    private List<ContentListDynamo> fetchOnlyMissingLists(SyncInfoWrapper syncInfoWrapper, String username) {
        List<ContentListDynamo> rawLists;
        List<String> listIds = getNotSynchronizedListsSince(username, syncInfoWrapper.syncInfo());
        log.info("Found {} lists to synchronize", listIds.size());
        rawLists = listIds.stream()
                .map(listId -> contentListDynamoService.getListById(listId, username))
                .toList();
        return rawLists;
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

    private List<ListDto> mergeEnrichedListsWithGivenOnes(SyncInfoWrapper syncInfoWrapper, List<ListDto> fetchedLists) {
        List<ListDto> listsToReturn = new ArrayList<>(fetchedLists);
        if(syncInfoWrapper.currentLists() == null || syncInfoWrapper.currentLists().isEmpty()) {
            return listsToReturn;
        }
        syncInfoWrapper.currentLists().stream()
                .filter(list -> !fetchedLists.contains(list))
                .forEach(listsToReturn::add);
        return listsToReturn;
    }

    private List<ListDto> enrichRawListsWithUserDetails(List<ContentListDynamo> rawLists, String username) {
        return new ArrayList<>(rawLists.parallelStream()
                .map((ContentListDynamo contentListDynamo) -> ListDto.of(contentListDynamo, enrichedItemsWithUserDetails(contentListDynamo, username)))
                .sorted(Comparator.comparing(ListDto::getName))
                .toList());
    }

    private List<String> getNotSynchronizedListsSince(String username, SyncInfo syncInfo) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        List<SyncInfo> lastSynchronizations = userDynamo.getLastSynchronization();
        if(!lastSynchronizationContains(username, syncInfo)) {
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

    private boolean lastSynchronizationContains(String username, SyncInfo syncInfo) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        if(syncInfo == null) {
            return false;
        }
        return userDynamo.getLastSynchronization().contains(syncInfo);
    }


    private void markUserLoggedIn(String username) {
        UserDynamo userDynamo = userDynamoDb.getItemById(username).orElseThrow(NoSuchUserException::new);
        userDynamo.markedLoggedIn();
        userDynamoDb.saveItem(userDynamo);
    }

    private List<SimpleItemDtoWithUserDetails> enrichedItemsWithUserDetails(ContentListDynamo contentList, String username) {
        int numberOfItemsToParsed = Math.min(Integer.parseInt(PAGE_SIZE), contentList.getItems().size());
        List<SimpleItem> itemsToParse = contentList.getItems().subList(0, numberOfItemsToParsed);
        List<SimpleItemDtoWithUserDetails> detailsForItems = new ArrayList<>();
        switch (contentList.getContentListType()) {
            case BOOK_LIST -> detailsForItems = new ArrayList<>(bookFacade.getDetailsForItems(itemsToParse, username));
            case MOVIE_LIST -> detailsForItems = new ArrayList<>(movieFacade.getDetailsForItems(itemsToParse, username));
            case GAME_LIST -> detailsForItems = new ArrayList<>(gameFacade.getDetailsForItems(itemsToParse, username));
        }
        if(contentList.getItems().size() > Integer.parseInt(PAGE_SIZE)) {
            List<SimpleItem> simpleItemsWithoutDetails = contentList.getItems().subList(numberOfItemsToParsed, contentList.getItems().size());
            List<SimpleItemDtoWithUserDetails> list = simpleItemsWithoutDetails.stream()
                    .map(simpleItem -> new SimpleItemDtoWithUserDetails(null, simpleItem))
                    .toList();
            detailsForItems.addAll(list);
        }
        return detailsForItems;
    }

}
