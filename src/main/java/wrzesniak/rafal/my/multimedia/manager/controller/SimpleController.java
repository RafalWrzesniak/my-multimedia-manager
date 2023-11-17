package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("simple")
public class SimpleController {

    @GetMapping("/getMe")
    public ResponseEntity<String> callMe(String requestName) {
        log.info("Inside of controller");
        return ResponseEntity.ok(requestName.toUpperCase());
    }


}
