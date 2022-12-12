package wrzesniak.rafal.my.multimedia.manager.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.nio.file.Path;

public class StringFunctions {

    @SneakyThrows
    public static JsonNode parseString(String string) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(string);
    }

    public static String slash(String string) {
        return "/".concat(string);
    }

    public static String fileNameOf(Path filePath) {
        String fileName = filePath.getFileName().toString();
        if(fileName.matches("^.+\\.\\w.+$")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }

}
