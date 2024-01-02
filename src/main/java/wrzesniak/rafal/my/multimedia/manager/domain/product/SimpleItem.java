package wrzesniak.rafal.my.multimedia.manager.domain.product;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@With
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class SimpleItem implements Product {

    private String id;
    private String title;
    private String webImageUrl;

    public static SimpleItem of(Product product) {
        return SimpleItem.builder()
                .id(product.getId())
                .title(product.getDisplayedTitle())
                .webImageUrl(product.getWebImageUrl())
                .build();
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayedTitle() {
        return title;
    }
}
