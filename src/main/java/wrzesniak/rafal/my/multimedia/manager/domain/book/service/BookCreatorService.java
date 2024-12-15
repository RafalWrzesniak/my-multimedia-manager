package wrzesniak.rafal.my.multimedia.manager.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BookNotCreatedException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductCreatorService;
import wrzesniak.rafal.my.multimedia.manager.web.lubimyczytac.LubimyCzytacService;

import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCreatorService implements ProductCreatorService<BookWithUserDetailsDto> {

    private final DefaultDynamoRepository<BookWithUserDetailsDto, BookUserDetailsDynamo, BookDynamo> bookDynamoRepository;
    private final LubimyCzytacService lubimyCzytacService;

    @Override
    public BookWithUserDetailsDto createProductFromUrl(URL lubimyCzytacBookUrl, String username) {
        return lubimyCzytacService.createBookDtoFromUrl(lubimyCzytacBookUrl)
                .map(DtoMapper::mapToBook)
                .map(bookDynamo -> bookDynamoRepository.saveProduct(bookDynamo, username))
                .orElseThrow(BookNotCreatedException::new);
    }

}
