package wrzesniak.rafal.my.multimedia.manager.util;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import wrzesniak.rafal.my.multimedia.manager.domain.game.objects.GamePlatform;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GamePlatformSetConverter implements AttributeConverter<Set<GamePlatform>> {

    @Override
    public AttributeValue transformFrom(Set<GamePlatform> input) {
        return AttributeValue.builder().s(input.toString().substring(1, input.toString().length()-1)).build();
    }

    @Override
    public Set<GamePlatform> transformTo(AttributeValue input) {
        return Arrays.stream(input.s().split(", "))
                .map(s -> s.replaceAll(" ", "_"))
                .map(this::createGamePlatform)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Optional<GamePlatform> createGamePlatform(String s) {
        try {
            return Optional.of(GamePlatform.valueOf(s));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public EnhancedType<Set<GamePlatform>> type() {
        return EnhancedType.setOf(GamePlatform.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
