package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.user.*;
import wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder;

import static wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder.TOKEN_HEADER;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final JwtTokenDecoder jwtTokenDecoder;
    private final UserService userService;

    @PostMapping("/lists")
    public UserLists getNeededListsData(@RequestHeader(TOKEN_HEADER) String jwtToken, @RequestBody SyncInfoWrapper syncInfoWrapper) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return userService.fetchNeededListData(username, syncInfoWrapper);
    }

    @PostMapping("/sync")
    public void setSynchronization(@RequestHeader(TOKEN_HEADER) String jwtToken, @RequestBody SyncInfo syncInfo) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        userService.setSynchronizationInfo(username, syncInfo);
    }


    @PostMapping("/register")
    public UserDynamo registerNewUser(@RequestParam String username,
                                      @RequestParam String preferredUsername,
                                      @RequestParam String email) {
        return userService.createNewUser(username, preferredUsername, email);
    }

}
