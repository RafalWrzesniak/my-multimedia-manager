package wrzesniak.rafal.my.multimedia.manager.domain.book.author;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Book;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    @OneToMany
    @ToString.Exclude
    @JsonBackReference
    private List<Book> writtenBooks;

    private LocalDate createdOn;

    public void addWrittenBook(Book book) {
        writtenBooks.add(book);
    }
}
