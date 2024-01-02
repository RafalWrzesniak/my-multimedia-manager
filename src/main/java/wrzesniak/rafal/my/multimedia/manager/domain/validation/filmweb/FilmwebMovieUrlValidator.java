package wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URL;

public class FilmwebMovieUrlValidator implements ConstraintValidator<FilmwebMovieUrl, URL> {

    @Override
    public void initialize(FilmwebMovieUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(URL filmwebUrl, ConstraintValidatorContext constraintValidatorContext) {
        return filmwebUrl == null || filmwebUrl.toString().matches("^https://www\\.filmweb\\.pl/(film|serial)/.+");
    }
}
