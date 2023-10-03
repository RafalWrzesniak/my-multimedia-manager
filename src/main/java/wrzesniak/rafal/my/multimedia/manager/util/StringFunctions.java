package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;

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
        if(titleWithDate == null || titleWithDate.isEmpty()) {
            return titleWithDate;
        }
        int braceIndex = titleWithDate.lastIndexOf(" (");
        return braceIndex != -1 ? titleWithDate.substring(0, braceIndex) : titleWithDate;
    }

    public static String withRemovedSlashes(String string) {
        return string.replaceAll("/", "");
    }

    @SneakyThrows
    public static URL toURL(String url) {
        return new URL(url.replaceAll(" ", "+"));
    }
    
    public static String toSnakeCase(String camelCaseString) {
        StringBuilder output = new StringBuilder();
        output.append(Character.toLowerCase(camelCaseString.charAt(0)));
        for (int i = 1; i < camelCaseString.length(); i++) {
            char currentChar = camelCaseString.charAt(i);
            if(Character.isUpperCase(currentChar)) {
                output.append("_");
            }
            output.append(Character.toLowerCase(currentChar));
        }
        return output.toString();
    }

    public static String firstNotEmpty(String title1, String title2) {
        return title1 != null && !title1.isEmpty() ? title1 : title2;
    }

}
