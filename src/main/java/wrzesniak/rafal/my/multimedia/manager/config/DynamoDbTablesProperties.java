package wrzesniak.rafal.my.multimedia.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Data
@Configuration("dynamoDbProperties")
@ConfigurationProperties(prefix = "dynamodb.table")
public class DynamoDbTablesProperties {

    private String users;
    private String contentLists;
    private String books;
    private String bookUserDetails;
    private String movies;
    private String movieUserDetails;
    private String games;
    private String gameUserDetails;

}
