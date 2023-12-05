package wrzesniak.rafal.my.multimedia.manager.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDto;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac.LubimyCzytacService;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCreatorService implements ProductCreatorService<BookWithUserDetailsDto> {

    private final DefaultDynamoRepository<BookWithUserDetailsDto, BookUserDetailsDynamo, BookDynamo> bookDynamoRepository;
    private final LubimyCzytacService lubimyCzytacService;

    public BookWithUserDetailsDto createBookFromDto(BookDto bookDto, String username) {
        Optional<BookWithUserDetailsDto> bookInDataBase = bookDynamoRepository.getById(bookDto.getUrl(), username);
        if(bookInDataBase.isPresent()) {
            log.info("Book already exist with dto: {}", bookDto);
            bookDynamoRepository.createOrUpdateUserDetailsFor(bookDto.getUrl(), username);
            return bookInDataBase.get();
        }
        BookDynamo bookDynamo = DtoMapper.mapToBook(bookDto);
        BookWithUserDetailsDto savedBook = bookDynamoRepository.saveProduct(bookDynamo, username);
        log.info("Book created from URL: {}", savedBook);
        return savedBook;
    }

    @Override
    public BookWithUserDetailsDto createProductFromUrl(URL lubimyCzytacBookUrl, String username) {
        return lubimyCzytacService.createBookDtoFromUrl(lubimyCzytacBookUrl)
                .map((BookDto bookDto) -> createBookFromDto(bookDto, username))
                .orElseThrow(BookNotCreatedException::new);
    }

}
