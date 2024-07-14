package wrzesniak.rafal.my.multimedia.manager.domain.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.ISBN;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDto;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class DtoMapper {

    public static BookDynamo mapToBook(BookDto bookDto) {
        return BookDynamo.builder()
                .title(bookDto.getName())
                .author(bookDto.getAuthor().getName())
                .category(bookDto.getGenre().substring(bookDto.getGenre().lastIndexOf("/") + 1))
                .description(bookDto.getDescription())
                .publisher(bookDto.getPublisher())
                .numberOfPages(bookDto.getNumberOfPages())
                .isbn(ISBN.of(bookDto.getIsbn()))
                .lubimyCzytacUrl(toURL(bookDto.getUrl()))
                .datePublished(bookDto.getDatePublished())
                .series(bookDto.getSeries())
                .createdOn(LocalDateTime.now())
                .webImageUrl(bookDto.getImage())
                .build();
    }

    public static GameDynamo mapToGame(GameDto gameDto) {
        return GameDynamo.builder()
                .title(gameDto.getName())
                .gryOnlineUrl(gameDto.getUrl())
                .description(gameDto.getDescription())
                .ratingValue(Optional.ofNullable(gameDto.getAggregateRating()).map(GameDto.AggregateRating::ratingValue).orElse(BigDecimal.ZERO))
                .ratingCount(Optional.ofNullable(gameDto.getAggregateRating()).map(GameDto.AggregateRating::ratingCount).orElse(0))
                .studio(gameDto.getAuthor().name())
                .publisher(gameDto.getPublisher())
                .playModes(new HashSet<>(gameDto.getPlayMode()))
                .gamePlatform(new HashSet<>(gameDto.getGamePlatform()))
                .genreList(new HashSet<>(gameDto.getGenre()))
                .releaseDate(gameDto.getReleaseDate())
                .webImageUrl(gameDto.getImage().toString())
                .createdOn(LocalDateTime.now())
                .build();
    }
}
