package wrzesniak.rafal.my.multimedia.manager.domain.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;
import wrzesniak.rafal.my.multimedia.manager.domain.content.Imagable;
import wrzesniak.rafal.my.multimedia.manager.util.IsbnConverter;
import wrzesniak.rafal.my.multimedia.manager.util.SeriesConverter;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book implements Imagable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    private String title;
    private String category;
    private String description;
    private String publisher;
    private int numberOfPages;
    @EqualsAndHashCode.Include
    @Convert(converter = IsbnConverter.class)
    private ISBN isbn;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Author author;
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private URL lubimyCzytacUrl;
    @Convert(converter = SeriesConverter.class)
    private Series series;

    private LocalDate datePublished;
    private LocalDate createdOn;

    @JsonIgnore
    @Override
    public String getUniqueId() {
        return isbn.isEmpty() ? id.toString() : isbn.getValue();
    }

    public void setAuthor(Author author) {
        this.author = author;
        author.addWrittenBook(this);
    }
}
