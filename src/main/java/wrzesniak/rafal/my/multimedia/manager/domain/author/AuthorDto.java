package wrzesniak.rafal.my.multimedia.manager.domain.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private String name;
    private URL url;
}