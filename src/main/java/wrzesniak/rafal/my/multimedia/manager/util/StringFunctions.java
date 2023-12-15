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

    public static int findLastDigitIndex(String input) {
        int lastIndex = input.length();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isDigit(currentChar)) {
                lastIndex = i;
            }
        }
        return lastIndex;
    }
}
