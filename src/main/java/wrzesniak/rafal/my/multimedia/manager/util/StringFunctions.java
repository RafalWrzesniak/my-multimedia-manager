package wrzesniak.rafal.my.multimedia.manager.util;

import java.io.File;

public class StringFunctions {

    public static String slash(String string) {
        return "/".concat(string);
    }

    public static String fileNameOf(File file) {
        String fileName = file.getName();
        if(fileName.matches("^.+\\.\\w.+$")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }

    public static String withRemovedDate(String titleWithDate) {
        int braceIndex = titleWithDate.lastIndexOf(" (");
        return braceIndex != -1 ? titleWithDate.substring(0, braceIndex) : titleWithDate;
    }
}
