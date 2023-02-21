package wrzesniak.rafal.my.multimedia.manager.domain.book;

import static java.lang.String.format;

public record Series(String name,
                     int position) {

    @Override
    public String toString() {
        return format("%s (tom %d)", name, position);
    }
}
