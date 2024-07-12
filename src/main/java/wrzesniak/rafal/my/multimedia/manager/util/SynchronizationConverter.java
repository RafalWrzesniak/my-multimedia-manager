package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.user.SyncInfo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SynchronizationConverter implements AttributeConverter<List<SyncInfo>> {

    @Override
    public AttributeValue transformFrom(List<SyncInfo> input) {
        String preparedString = input.stream()
                .map(syncInfo -> syncInfo.syncTimestamp().toString() + " | " + syncInfo.changedListIds().toString().substring(1, syncInfo.changedListIds().toString().length() - 1))
                .reduce("", (s1, s2) -> s1 + " || " + s2);
        return AttributeValue.builder()
                .s(preparedString.length() > 4 ? preparedString.substring(4) : "")
                .build();
    }

    @Override
    public List<SyncInfo> transformTo(AttributeValue input) {
        if(input.s().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(input.s().split(" \\|\\| "))
            .map(s -> {
                String[] splited = s.split(" \\| ");
                return new SyncInfo(LocalDateTime.parse(splited[0]), Arrays.asList(splited[1].split(", ")));
            })
            .collect(Collectors.toList());
    }

    @Override
    public EnhancedType<List<SyncInfo>> type() {
        return EnhancedType.listOf(SyncInfo.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
