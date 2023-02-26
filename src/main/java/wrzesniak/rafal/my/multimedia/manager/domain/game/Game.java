package wrzesniak.rafal.my.multimedia.manager.domain.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import wrzesniak.rafal.my.multimedia.manager.domain.content.Imagable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Imagable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;
    private URL gryOnlineUrl;
    private String description;
    private BigDecimal ratingValue;
    private Integer ratingCount;
    private String studio;
    private String publisher;
    @ElementCollection(targetClass = String.class)
    private Set<PlayMode> playModes;
    @ElementCollection(targetClass = String.class)
    private Set<GamePlatform> gamePlatform;
    @ElementCollection
    private Set<String> genreList;
    private LocalDate releaseDate;

    @CreationTimestamp
    private LocalDate createdOn;

    @JsonIgnore
    @Override
    public String getUniqueId() {
        return "game_".concat(id.toString());
    }

}
