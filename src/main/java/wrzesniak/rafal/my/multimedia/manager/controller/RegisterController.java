package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.user.RegistrationService;

import javax.validation.Valid;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegistrationService registrationService;

    @PostMapping("/")
    public UserDetails registerNewUser(@Valid LoginCredentials credentials) {
        log.info("Trying to register new user: {}", credentials.getUsername());
        return registrationService.registerNewUserAccount(credentials);
    }

}
