package wrzesniak.rafal.my.multimedia.manager.domain.validation.imdb;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImdbIdValidator implements ConstraintValidator<ImdbId, String> {

    @Override
    public void initialize(ImdbId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String imdbId, ConstraintValidatorContext constraintValidatorContext) {
        return imdbId.matches("^(nm|tt)\\d{5,9}$");
    }
}
