package wrzesniak.rafal.my.multimedia.manager.domain.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorDto;

import java.net.URL;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String name;
    private long isbn;
    private LocalDate datePublished;
    private String genre;
    private int numberOfPages;
    private URL image;
    private AuthorDto author;
    private String description;
    private String publisher;
    private URL url;

}
