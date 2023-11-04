package wrzesniak.rafal.my.multimedia.manager.util;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.PlayMode;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayModeSetConverter implements AttributeConverter<Set<PlayMode>> {

    @Override
    public AttributeValue transformFrom(Set<PlayMode> input) {
        return AttributeValue.builder().s(input.toString().substring(1, input.toString().length()-1)).build();
    }

    @Override
    public Set<PlayMode> transformTo(AttributeValue input) {
        return Arrays.stream(input.s().split(", "))
                .map(PlayMode::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public EnhancedType<Set<PlayMode>> type() {
        return EnhancedType.setOf(PlayMode.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
