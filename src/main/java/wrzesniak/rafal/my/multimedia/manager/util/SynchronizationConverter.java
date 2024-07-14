package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.user.SyncInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SynchronizationConverter implements AttributeConverter<List<SyncInfo>> {

    @Override
    public AttributeValue transformFrom(List<SyncInfo> input) {
        return AttributeValue.builder()
                .l(input.stream()
                        .map(syncInfo -> AttributeValue.builder()
                                .m(Map.of(syncInfo.syncTimestamp().toString(), AttributeValue.fromSs(syncInfo.changedListIds())))
                                .build())
                        .toList()
                )
                .build();
    }

    @Override
    public List<SyncInfo> transformTo(AttributeValue input) {
        return new ArrayList<>(input.l().stream()
                .map(AttributeValue::m)
                .map(stringAttributeValueMap -> new SyncInfo(LocalDateTime.parse(stringAttributeValueMap.keySet().stream().findFirst().orElseThrow()), stringAttributeValueMap.values().stream()
                        .map(AttributeValue::ss)
                        .flatMap(Collection::stream)
                        .toList()))
                .toList());
    }

    @Override
    public EnhancedType<List<SyncInfo>> type() {
        return EnhancedType.listOf(SyncInfo.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }
}
