package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentListType {

    BOOK_LIST("Wszystkie książki"),
    MOVIE_LIST("Wszystkie filmy"),
    GAME_LIST("Wszystkie gry");

    private final String allProductsListName;

}
