package wrzesniak.rafal.my.multimedia.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wrzesniak.rafal.my.multimedia.manager.domain.book.Series;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Converter
@Component
public class SeriesConverter implements AttributeConverter<Series, String> {

    @Override
    public String convertToDatabaseColumn(Series series) {
        return series != null ? series.toString() : null;
    }

    @Override
    public Series convertToEntityAttribute(String series) {
        if(series == null) return null;
        Pattern pattern = Pattern.compile("(.+?) \\(tom (\\d+)\\)");
        Matcher matcher = pattern.matcher(series);
        if(!matcher.find()) {
            log.warn("Could not parse string `{}` to Series object", series);
            return null;
        }
        return new Series(matcher.group(1), Integer.parseInt(matcher.group(2)));
    }
}
