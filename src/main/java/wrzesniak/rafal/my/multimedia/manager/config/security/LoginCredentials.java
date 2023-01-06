package wrzesniak.rafal.my.multimedia.manager.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class LoginCredentials {

    public static final String USERNAME_VALIDATION_REGEX = "^[a-zA-Z][\\w.@_]{3,50}$";
    public static final String USERNAME_VALIDATION_MESSAGE = "Username must be between 4-50 letters, start with letter and contain only letters, digits, dots, at signs and underscore";
    public static final String PASSWORD_VALIDATION_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    public static final String PASSWORD_VALIDATION_MESSAGE = "Password must has minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character";

    @Pattern(regexp = USERNAME_VALIDATION_REGEX, message = USERNAME_VALIDATION_MESSAGE)
    private String username;
    @Pattern(regexp = PASSWORD_VALIDATION_REGEX, message = PASSWORD_VALIDATION_MESSAGE)
    private String password;

}
