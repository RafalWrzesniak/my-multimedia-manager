package wrzesniak.rafal.my.multimedia.manager.domain.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookFormat;
import wrzesniak.rafal.my.multimedia.manager.domain.book.service.BookCreatorService;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookListWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.product.DefaultProductService;

import static wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListType.BOOK_LIST;

@Slf4j
@Service
public class BookFacade extends DefaultProductService<BookWithUserDetailsDto, BookUserDetailsDynamo, BookListWithUserDetails, BookDynamo> {


    public BookFacade(DefaultDynamoRepository<BookWithUserDetailsDto, BookUserDetailsDynamo, BookDynamo> bookDynamoRepository,
                      BookCreatorService bookCreatorService,
                      ContentListDynamoService contentListDynamoService) {
        super(BOOK_LIST, BookListWithUserDetails::of, contentListDynamoService, bookCreatorService, bookDynamoRepository);
    }

    public void setFormatForUserBook(String bookId, BookFormat bookFormat, String username) {
        BookUserDetailsDynamo bookDetails = super.getProductUserDetails(bookId, username);
        bookDetails.setBookFormat(bookFormat);
        log.info("Marking book `{}` as reading on {}", bookId, bookFormat);
        super.updateUserProductDetails(bookDetails);
    }

}
