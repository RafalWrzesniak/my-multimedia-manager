package wrzesniak.rafal.my.multimedia.manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.BookDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.book.user.details.BookWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DefaultDynamoRepository;
import wrzesniak.rafal.my.multimedia.manager.domain.dynamodb.DynamoDbClientGeneric;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GameDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameUserDetailsDtoDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.game.user.details.GameWithUserDetailsDto;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.MovieDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieUserDetailsDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.user.details.MovieWithUserDetailsDto;

@Configuration
@RequiredArgsConstructor
public class DynamoDbConfiguration {

    @Bean
    public DynamoDbClientGeneric<ContentListDynamo> contentListsDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), ContentListDynamo.class, "ContentLists");
    }

    @Bean
    public DynamoDbClientGeneric<MovieDynamo> movieDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), MovieDynamo.class, "Movies");
    }

    @Bean
    public DynamoDbClientGeneric<MovieUserDetailsDynamo> movieUserDetailsDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), MovieUserDetailsDynamo.class, "MovieUserDetails");
    }

    @Bean
    public DynamoDbClientGeneric<BookDynamo> bookDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), BookDynamo.class, "Books");
    }

    @Bean
    public DynamoDbClientGeneric<BookUserDetailsDynamo> bookUserDetailsDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), BookUserDetailsDynamo.class, "BookUserDetails");
    }


    @Bean
    public DynamoDbClientGeneric<GameDynamo> gameDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), GameDynamo.class, "Games");
    }

    @Bean
    public DynamoDbClientGeneric<GameUserDetailsDtoDynamo> gameUserDetailsDynamoDb() {
        return new DynamoDbClientGeneric<>(enhancedClient(), GameUserDetailsDtoDynamo.class, "GameUserDetails");
    }

    @Bean
    public DefaultDynamoRepository<MovieWithUserDetailsDto, MovieUserDetailsDynamo, MovieDynamo> movieDynamoRepository() {
        return new DefaultDynamoRepository<>(movieDynamoDb(), movieUserDetailsDynamoDb(), MovieUserDetailsDynamo::new, MovieWithUserDetailsDto::of);
    }

    @Bean
    public DefaultDynamoRepository<BookWithUserDetailsDto, BookUserDetailsDynamo, BookDynamo> bookDynamoRepository() {
        return new DefaultDynamoRepository<>(bookDynamoDb(), bookUserDetailsDynamoDb(), BookUserDetailsDynamo::new, BookWithUserDetailsDto::of);
    }

    @Bean
    public DefaultDynamoRepository<GameWithUserDetailsDto, GameUserDetailsDtoDynamo, GameDynamo> gameDynamoRepository() {
        return new DefaultDynamoRepository<>(gameDynamoDb(), gameUserDetailsDynamoDb(), GameUserDetailsDtoDynamo::new, GameWithUserDetailsDto::of);
    }


    private DynamoDbEnhancedClient enhancedClient() {
        DynamoDbClient dynamoClient = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoClient)
                .build();
    }
}
