package wrzesniak.rafal.my.multimedia.manager.domain.validation.lubimyczytac;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URL;

public class LubimyCzytacUrlValidator implements ConstraintValidator<LubimyCzytacUrl, URL> {

    @Override
    public void initialize(LubimyCzytacUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(URL lubimyCzytacUrl, ConstraintValidatorContext constraintValidatorContext) {
        return lubimyCzytacUrl == null || lubimyCzytacUrl.toString().matches("^https://lubimyczytac\\.pl/ksiazka/.+");
    }

}
