package wrzesniak.rafal.my.multimedia.manager.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ActorContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static javax.persistence.GenerationType.IDENTITY;
import static wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials.*;

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
    private List<ActorContentList> actorList;


    public MovieContentList addNewMovieList(String listName) {
        Optional<MovieContentList> existingList = getMovieContentListByName(listName);
        if(existingList.isPresent()) {
            return existingList.get();
        }
        MovieContentList movieContentList = new MovieContentList(listName);
        movieLists.add(movieContentList);
        return movieContentList;
    }

    public ActorContentList addNewActorList(String listName) {
        Optional<ActorContentList> existingList = getActorContentListByName(listName);
        if(existingList.isPresent()) {
            return existingList.get();
        }
        ActorContentList actorContentList = new ActorContentList(listName);
        actorList.add(actorContentList);
        return actorContentList;
    }

    private <T> Predicate<ContentList<T>> listNameIsEqualTo(String listName) {
        return list -> list.getName().equals(listName);
    }

    public Optional<MovieContentList> getMovieContentListByName(String listName) {
        return movieLists.stream()
                .filter(listNameIsEqualTo(listName))
                .findFirst();
    }
    public Optional<ActorContentList> getActorContentListByName(String listName) {
        return actorList.stream()
                .filter(listNameIsEqualTo(listName))
                .findFirst();
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
