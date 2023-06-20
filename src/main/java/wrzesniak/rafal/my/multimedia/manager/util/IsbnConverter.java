package wrzesniak.rafal.my.multimedia.manager.util;

import wrzesniak.rafal.my.multimedia.manager.domain.book.objects.ISBN;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class IsbnConverter implements AttributeConverter<ISBN, String> {

    @Override
    public String convertToDatabaseColumn(ISBN isbn) {
        return isbn.getValue();
    }

    @Override
    public ISBN convertToEntityAttribute(String string) {
        return ISBN.of(string);
    }
}
