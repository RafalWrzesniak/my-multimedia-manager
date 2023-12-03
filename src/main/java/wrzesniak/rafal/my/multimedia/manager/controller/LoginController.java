package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;

import javax.validation.Valid;

@Slf4j
@Validated
@CrossOrigin
@RestController
public class LoginController {

    @PostMapping("/login")
    public void login(@RequestBody @Valid LoginCredentials credentials, @RequestHeader MultiValueMap<String, String> headers) {
        headers.forEach((key, value) -> {
            log.info(String.format(
                    "Header '%s' = %s", key, String.join("|", value)));
        });
    }

}
