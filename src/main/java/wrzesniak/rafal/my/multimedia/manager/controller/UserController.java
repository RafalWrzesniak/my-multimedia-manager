package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.book.BookFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.ListDto;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.SimpleItemDtoWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.GameFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.MovieFacade;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;
import wrzesniak.rafal.my.multimedia.manager.domain.user.SyncInfo;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;
import wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.controller.BaseProductController.PAGE_SIZE;
import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.*;
import static wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder.TOKEN_HEADER;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final ContentListDynamoService contentListDynamoService;
    private final GameFacade gameFacade;
    private final BookFacade bookFacade;
    private final MovieFacade movieFacade;
    private final JwtTokenDecoder jwtTokenDecoder;
    private final UserService userService;

    @GetMapping("/lists")
    public List<ListDto> getGeneralListsInfo(@RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        log.info("Starts fetching basic list info for {}", username);
        List<ContentListDynamo> allContentLists = contentListDynamoService.getAllContentLists(username);
        log.info("Found {} lists", allContentLists.size());
        if(allContentLists.isEmpty()) {
            allContentLists = userService.createAllContentListForNewUser(username);
        }
        userService.markUserLoggedIn(username);
        List<ListDto> list = allContentLists.parallelStream()
                .map((ContentListDynamo contentListDynamo) -> ListDto.of(contentListDynamo, enrichedItemsWithUserDetails(contentListDynamo, username)))
                .sorted(Comparator.comparing(ListDto::getName))
                .toList();
        log.info("Enriched lists with user details");
        return list;
    }

    @PostMapping("/sync")
    public void setSynchronization(@RequestHeader(TOKEN_HEADER) String jwtToken, @RequestBody SyncInfo syncInfo) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        userService.setSynchronizationInfo(username, syncInfo);
    }

    @GetMapping("/sync")
    public SyncInfo getLastSynchronizationInfo(@RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return userService.getLastSynchronizationInfo(username);
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

    @PostMapping("/register")
    public UserDynamo registerNewUser(@RequestParam String username,
                                      @RequestParam String preferredUsername,
                                      @RequestParam String email) {
        return userService.createNewUser(username, preferredUsername, email);
    }

    public void warmUpCaches(String username) {
        String bookListId = contentListDynamoService.getAllProductsList(BOOK_LIST, username).getListId();
        new Thread(() -> bookFacade.getAllProductsForList(bookListId, username)).start();
        String movieListId = contentListDynamoService.getAllProductsList(MOVIE_LIST, username).getListId();
        new Thread(() -> movieFacade.getAllProductsForList(movieListId, username)).start();
        String gameListId = contentListDynamoService.getAllProductsList(GAME_LIST, username).getListId();
        new Thread(() -> gameFacade.getAllProductsForList(gameListId, username)).start();
    }
}
