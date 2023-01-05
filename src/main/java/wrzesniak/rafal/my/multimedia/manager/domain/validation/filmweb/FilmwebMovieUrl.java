package wrzesniak.rafal.my.multimedia.manager.domain.validation.filmweb;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = FilmwebMovieUrlValidator.class)
@Documented
public @interface FilmwebMovieUrl {

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String message() default "Invalid filmweb movie url";

}
