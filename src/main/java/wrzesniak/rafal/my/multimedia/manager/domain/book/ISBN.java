package wrzesniak.rafal.my.multimedia.manager.domain.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@EqualsAndHashCode
public class ISBN {

    private static final Pattern NOT_DIGITS_OR_NOT_X = Pattern.compile( "[^\\dX]" );

    @Getter
    private final String value;

    private ISBN(String value) {
        this.value = value;
    }

    public static ISBN of(String isbn) {
        if(isbn == null) {
            return empty();
        }
        String digits = NOT_DIGITS_OR_NOT_X.matcher(isbn).replaceAll("");
        return isValid(digits) ? new ISBN(digits) : empty();
    }

    public static ISBN empty() {
        return new ISBN(null);
    }

    private static boolean isValid(String isbn) {
        return 13 == isbn.length() && checkChecksumISBN13(isbn);
    }

    /**
     * Check the digits for ISBN 13 using algorithm from
     * <a href="https://en.wikipedia.org/wiki/International_Standard_Book_Number#ISBN-13_check_digit_calculation">Wikipedia</a>.
     */
    private static boolean checkChecksumISBN13(String isbn) {
        int sum = 0;
        for ( int i = 0; i < isbn.length(); i++ ) {
            sum += ( isbn.charAt( i ) - '0' ) * ( i % 2 == 0 ? 1 : 3 );
        }

        return ( sum % 10 ) == 0;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return value == null;
    }

}
