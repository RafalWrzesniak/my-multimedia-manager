package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.movie.objects.SeriesInfo;

import java.util.Optional;

@Slf4j
@Component
public class SeriesInfoDynamoConverter implements AttributeConverter<SeriesInfo> {

    @Override
    public AttributeValue transformFrom(SeriesInfo series) {
        return Optional.ofNullable(series)
                .map(series1 ->  AttributeValue.builder().s(series1.toString()).build())
                .orElse(null);
    }

    @Override
    public SeriesInfo transformTo(AttributeValue input) {
        String seriesInfo = input.s();
        if(seriesInfo == null) return null;
        int seasonsCount = Integer.parseInt(seriesInfo.substring(seriesInfo.indexOf("=") + 1, seriesInfo.indexOf("=") + 2));
        int allEpisodesCount = Integer.parseInt(seriesInfo.substring(seriesInfo.lastIndexOf("=") + 1, seriesInfo.lastIndexOf("=") + 2));
        return new SeriesInfo(seasonsCount, allEpisodesCount);
    }

    @Override
    public EnhancedType<SeriesInfo> type() {
        return EnhancedType.of(SeriesInfo.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
