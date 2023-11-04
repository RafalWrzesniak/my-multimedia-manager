package wrzesniak.rafal.my.multimedia.manager.util;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.ISBN;

public class IsbnDynamoConverter implements AttributeConverter<ISBN> {

    @Override
    public AttributeValue transformFrom(ISBN input) {
        return AttributeValue.fromS(input.toString());
    }

    @Override
    public ISBN transformTo(AttributeValue input) {
        return ISBN.of(input.s());
    }

    @Override
    public EnhancedType<ISBN> type() {
        return EnhancedType.of(ISBN.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
