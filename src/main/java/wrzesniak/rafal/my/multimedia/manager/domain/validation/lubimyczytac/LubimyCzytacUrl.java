package wrzesniak.rafal.my.multimedia.manager.domain.validation.lubimyczytac;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import wrzesniak.rafal.my.multimedia.manager.domain.validation.gryonline.GryOnlineUrlValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = LubimyCzytacUrlValidator.class)
@Documented
public @interface LubimyCzytacUrl {

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String message() default "Invalid lubimy czytac url";

}
