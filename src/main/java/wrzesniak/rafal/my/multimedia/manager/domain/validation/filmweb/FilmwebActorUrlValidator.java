package wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URL;

public class FilmwebActorUrlValidator implements ConstraintValidator<FilmwebActorUrl, URL> {

    @Override
    public void initialize(FilmwebActorUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(URL filmwebUrl, ConstraintValidatorContext constraintValidatorContext) {
        return filmwebUrl.toString().matches("^https://www\\.filmweb\\.pl/person/.+");
    }
}
