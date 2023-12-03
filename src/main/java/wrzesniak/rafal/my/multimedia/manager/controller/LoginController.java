package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;

import javax.validation.Valid;

@Slf4j
@Validated
@CrossOrigin
@RestController
public class LoginController {

    @PostMapping("/login")
    public void login(@RequestParam @Valid LoginCredentials credentials) {
    }

}
