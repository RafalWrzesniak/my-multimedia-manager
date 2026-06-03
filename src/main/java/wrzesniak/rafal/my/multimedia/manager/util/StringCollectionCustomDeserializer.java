package wrzesniak.rafal.my.multimedia.manager.util;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public class StringCollectionCustomDeserializer extends ValueDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonToken token = p.currentToken();
        if (token == JsonToken.START_ARRAY) {
            List<String> strings = new ArrayList<>();
            while (p.nextToken() != JsonToken.END_ARRAY) {
                strings.add(p.getValueAsString());
            }
            return strings;
        } else if (token == JsonToken.VALUE_STRING) {
            return List.of(p.getValueAsString());
        } else {
            throw new IllegalArgumentException("Invalid value");
        }
    }

}
