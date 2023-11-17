package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("simple")
public class SimpleController {

    @GetMapping("/getMe/{requestName}")
    public ResponseEntity<String> callMe(@PathVariable String requestName) {
        log.info("Inside of controller");
        return ResponseEntity.ok(requestName.toUpperCase());
    }

}
