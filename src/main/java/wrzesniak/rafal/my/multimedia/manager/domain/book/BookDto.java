package wrzesniak.rafal.my.multimedia.manager.domain.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import wrzesniak.rafal.my.multimedia.manager.domain.author.AuthorDto;

import java.time.LocalDate;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String name;
    private String isbn;
    private LocalDate datePublished;
    private String genre;
    private int numberOfPages;
    private String image;
    private AuthorDto author;
    private String description;
    private String publisher;
    private String url;
    private BookFormat bookFormat;

}
