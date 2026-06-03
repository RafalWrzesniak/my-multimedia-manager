package wrzesniak.rafal.my.multimedia.manager.domain.book.objects;

public record Series(String name,
                     int position) {

    @Override
    public String toString() {
        return "%s (tom %d)".formatted(name, position);
    }
}
