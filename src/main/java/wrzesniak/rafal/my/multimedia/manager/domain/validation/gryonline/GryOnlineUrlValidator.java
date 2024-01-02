package wrzesniak.rafal.my.multimedia.manager.domain.validation.gryonline;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URL;

public class GryOnlineUrlValidator implements ConstraintValidator<GryOnlineUrl, URL> {

    @Override
    public void initialize(GryOnlineUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(URL gryOnlineUrl, ConstraintValidatorContext constraintValidatorContext) {
        return gryOnlineUrl == null || gryOnlineUrl.toString().matches("^https://www\\.gry-online\\.pl/gry/.+");
    }

}
