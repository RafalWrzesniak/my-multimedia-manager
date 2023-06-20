package wrzesniak.rafal.my.multimedia.manager.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.content.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static javax.persistence.GenerationType.IDENTITY;
import static wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials.*;
import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.*;

@With
@Data
@Entity
@Builder
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Pattern(regexp = USERNAME_VALIDATION_REGEX, message = USERNAME_VALIDATION_MESSAGE)
    private String username;

    @JsonIgnore
    @Pattern(regexp = PASSWORD_VALIDATION_REGEX, message = PASSWORD_VALIDATION_MESSAGE)
    private String password;
    private UserRole userRole;
    private boolean enabled;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<MovieContentList> movieLists;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<BookContentList> bookLists;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<GameContentList> gameLists;

    public <T extends BaseContentList<?>> T addNewContentList(String listName, ContentListType type) {
        List<T> contentLists = findContentListByType(type);
        Optional<T> existingList = contentLists.stream()
                .filter(list -> list.getName().equals(listName))
                .findFirst();
        if(existingList.isPresent()) {
            return existingList.get();
        }
        BaseContentList<?> list;
        if(type.equals(MOVIE_LIST)) {
            list = new MovieContentList(listName);
        } else if (type.equals(BOOK_LIST)) {
            list = new BookContentList(listName);
        } else if(type.equals(GAME_LIST)) {
            list = new GameContentList(listName);
        } else {
            return null;
        }
        contentLists.add((T) list);
        return (T) list;
    }

    public <T extends BaseContentList<?>> void removeContentList(String listName, ContentListType type) {
        List<T> contentLists = findContentListByType(type);
        Optional<T> list = getContentListByName(listName, type);
        list.ifPresent(contentLists::remove);
    }

    public <T extends BaseContentList<?>> Optional<T> getContentListByName(String listName, ContentListType type) {
        List<T> contentLists = findContentListByType(type);
        return contentLists.stream()
                .filter(list -> list.getName().equals(listName))
                .findFirst();
    }

    public <T extends BaseContentList<?>> List<T> findContentListByType(ContentListType type) {
        List<?> list;
        if(type.equals(MOVIE_LIST)) {
            list = movieLists;
        } else if(type.equals(BOOK_LIST)) {
            list = bookLists;
        } else if(type.equals(GAME_LIST)) {
            list = gameLists;
        } else {
            throw new IllegalArgumentException();
        }
        return (List<T>) list;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(userRole.name()));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
