package wrzesniak.rafal.my.multimedia.manager.util;

import java.nio.file.Path;

public class StringFunctions {

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

    public static String withRemovedDate(String titleWithDate) {
        return titleWithDate.substring(0, titleWithDate.indexOf(" ("));
    }
}
