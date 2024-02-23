package wrzesniak.rafal.my.multimedia.manager.domain.book.objects;

import lombok.*;
import wrzesniak.rafal.my.multimedia.manager.domain.book.author.AuthorDto;

import java.time.LocalDate;

@Data
@With
@Builder
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
    private Series series;

}
