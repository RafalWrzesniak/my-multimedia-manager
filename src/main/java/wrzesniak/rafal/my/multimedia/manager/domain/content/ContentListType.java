package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.Game;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.Movie;

@Getter
@RequiredArgsConstructor
public enum ContentListType {

    BOOK_LIST("Wszystkie książki", Book.class),
    MOVIE_LIST("Wszystkie filmy", Movie.class),
    GAME_LIST("Wszystkie gry", Game.class);

    private final String allProductsListName;
    private final Class<?> contentType;

}
