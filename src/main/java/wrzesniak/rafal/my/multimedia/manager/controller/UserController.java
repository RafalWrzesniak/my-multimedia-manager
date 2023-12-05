package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.ListDto;
import wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder.TOKEN_HEADER;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final ContentListDynamoService contentListDynamoService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @GetMapping("/lists")
    public List<ListDto> getGeneralListsInfo(@RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        log.info("Starts fetching basic list info for {}", username);
        List<ContentListDynamo> allContentLists = contentListDynamoService.getAllContentLists(username);
        if(allContentLists.isEmpty()) {
            allContentLists = createAllContentListForNewUser(username);
        }
        return allContentLists.stream()
                .map(ListDto::new)
                .sorted(Comparator.comparing(ListDto::getName))
                .toList();
    }

    private List<ContentListDynamo> createAllContentListForNewUser(String username) {
        List<ContentListDynamo> allContentLists;
        allContentLists = Arrays.stream(ContentListType.values())
                .map(contentListType -> contentListDynamoService.createContentList(contentListType.getAllProductsListName(),
                        username, contentListType, true))
                .toList();
        log.info("New users list created successfully: {}", username);
        return allContentLists;
    }

}
