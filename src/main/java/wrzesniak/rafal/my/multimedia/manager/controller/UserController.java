package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.user.UserService;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/lists")
    public List<?> getGeneralListsInfo() {
        return Stream.of(
                        userService.getCurrentUser().getMovieLists().stream().map(MovieListWithUserDetails::of).sorted(Comparator.comparing(MovieListWithUserDetails::getName)).toList(),
                        userService.getCurrentUser().getGameLists().stream().map(GameListWithUserDetails::of).sorted(Comparator.comparing(GameListWithUserDetails::getName)).toList(),
                        userService.getCurrentUser().getBookLists().stream().map(BookListWithUserDetails::of).sorted(Comparator.comparing(BookListWithUserDetails::getName)).toList()
                )
                .flatMap(Collection::stream)
                .toList();
    }
}
