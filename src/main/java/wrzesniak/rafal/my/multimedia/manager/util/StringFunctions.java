package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.SneakyThrows;

import java.net.URL;

public class StringFunctions {

    public static String slash(String string) {
        return "/".concat(string);
    }

    @SneakyThrows
    public static URL toURL(String url) {
        return new URL(url.replaceAll(" ", "+"));
    }

    public static String firstNotEmpty(String title1, String title2) {
        return title1 != null && !title1.isEmpty() ? title1 : title2;
    }

    public static int findLastDigitIndexBeforeComma(String input) {
        int commaIndex = input.indexOf(",");
        int lastIndex = commaIndex != -1 ? commaIndex : input.length();
        String subInput = input.substring(0, lastIndex);
        for (int i = 0; i < subInput.length(); i++) {
            char currentChar = subInput.charAt(i);
            if (Character.isDigit(currentChar)) {
                lastIndex = i;
            }
        }
        return lastIndex;
    }
}
