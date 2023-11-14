package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.Series;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SeriesDynamoConverter implements AttributeConverter<Series> {

    @Override
    public AttributeValue transformFrom(Series series) {
        return Optional.ofNullable(series)
                .map(series1 ->  AttributeValue.builder().s(series1.toString()).build())
                .orElse(null);
    }

    @Override
    public Series transformTo(AttributeValue input) {
        String series = input.s();
        if(series == null) return null;
        Pattern pattern = Pattern.compile("(.+?) \\(tom (\\d+)\\)");
        Matcher matcher = pattern.matcher(series);
        if(!matcher.find()) {
            log.warn("Could not parse string `{}` to Series object", series);
            return null;
        }
        return new Series(matcher.group(1), Integer.parseInt(matcher.group(2)));
    }

    @Override
    public EnhancedType<Series> type() {
        return EnhancedType.of(Series.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
