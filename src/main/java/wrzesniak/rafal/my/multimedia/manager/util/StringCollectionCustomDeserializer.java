package wrzesniak.rafal.my.multimedia.manager.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringCollectionCustomDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
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
