package wrzesniak.rafal.my.multimedia.manager.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ActorContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static javax.persistence.GenerationType.IDENTITY;

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
    private String username;
    @JsonIgnore
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
        Optional<MovieContentList> existingList = movieLists.stream().filter(list -> list.getName().equals(listName)).findFirst();
        if(existingList.isPresent()) {
            return existingList.get();
        }
        MovieContentList movieContentList = new MovieContentList(listName);
        movieLists.add(movieContentList);
        return movieContentList;
    }

    public ActorContentList addNewActorList(String listName) {
        Optional<ActorContentList> existingList = actorList.stream().filter(list -> list.getName().equals(listName)).findFirst();
        if(existingList.isPresent()) {
            return existingList.get();
        }
        ActorContentList actorContentList = new ActorContentList(listName);
        actorList.add(actorContentList);
        return actorContentList;
    }

    public Optional<MovieContentList> getMovieContentListByName(String listName) {
        return movieLists.stream()
                .filter(movieContentList -> movieContentList.getName().equals(listName))
                .findFirst();
    }
    public Optional<ActorContentList> getActorContentListByName(String listName) {
        return actorList.stream()
                .filter(actorContentList -> actorContentList.getName().equals(listName))
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
