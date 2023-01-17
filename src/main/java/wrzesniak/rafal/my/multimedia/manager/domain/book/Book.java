package wrzesniak.rafal.my.multimedia.manager.domain.book;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.author.Author;

import javax.persistence.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String description;
    private String publisher;
    private int numberOfPages;
    @Column(unique = true)
    private long isbn;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Author author;
    @Column(unique = true)
    private URL lubimyCzytacUrl;

    private LocalDate datePublished;
    private LocalDate readOn;
    private LocalDate createdOn;

    public Path getImagePath() {
        return Path.of("images", "book", String.valueOf(isbn).concat(".jpg"));
    }

    public void setAuthor(Author author) {
        this.author = author;
        author.addWrittenBook(this);
    }
}
