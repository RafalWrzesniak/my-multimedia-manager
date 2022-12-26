package wrzesniak.rafal.my.multimedia.manager.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginCredentials {

    private String username;
    private String password;

}
