package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.Data;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieListWithUserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserLists {

    private List<BookListWithUserDetails> bookLists;
    private List<MovieListWithUserDetails> movieLists;
    private List<GameListWithUserDetails> gameLists;

    public UserLists(List<BookListWithUserDetails> bookLists, List<MovieListWithUserDetails> movieLists, List<GameListWithUserDetails> gameLists) {
        this.bookLists = new ArrayList<>(bookLists);
        this.movieLists = new ArrayList<>(movieLists);
        this.gameLists = new ArrayList<>(gameLists);
    }

    public boolean isEmpty() {
        return bookLists.isEmpty() && movieLists.isEmpty() && gameLists.isEmpty();
    }

    public <T> boolean contains(T list) {
        return switch (list) {
            case BookListWithUserDetails bookListWithUserDetails -> bookLists.contains(bookListWithUserDetails);
            case MovieListWithUserDetails movieListWithUserDetails -> movieLists.contains(movieListWithUserDetails);
            case GameListWithUserDetails gameListWithUserDetails -> gameLists.contains(gameListWithUserDetails);
            case null, default -> false;
        };
    }

    public List<Object> getAllLists() {
        List<Object> lists = new ArrayList<>();
        lists.addAll(bookLists);
        lists.addAll(movieLists);
        lists.addAll(gameLists);
        return lists;
    }

    public <T> void add(T list) {
        switch (list) {
            case BookListWithUserDetails bookListWithUserDetails -> bookLists.add(bookListWithUserDetails);
            case MovieListWithUserDetails movieListWithUserDetails -> movieLists.add(movieListWithUserDetails);
            case GameListWithUserDetails gameListWithUserDetails -> gameLists.add(gameListWithUserDetails);
            default -> throw new IllegalStateException("Unexpected value: " + list);
        };
    }
}
