package wrzesniak.rafal.my.multimedia.manager.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;

import javax.validation.Valid;

@Validated
@RestController
public class LoginController {

    @PostMapping("/login")
    public void login(@Valid LoginCredentials credentials) {
    }

}
