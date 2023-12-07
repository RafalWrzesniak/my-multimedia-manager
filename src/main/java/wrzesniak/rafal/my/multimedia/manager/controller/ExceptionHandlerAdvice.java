package wrzesniak.rafal.my.multimedia.manager.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wrzesniak.rafal.my.multimedia.manager.domain.error.BasicApplicationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessageTemplate)
                .toList();

        return buildResponseEntity(messages);
    }

    @ExceptionHandler(BasicApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(BasicApplicationException e) {
        return buildResponseEntity(List.of(e.getClass().getSimpleName()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(BindException e) {
        List<String> errors = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return buildResponseEntity(errors);
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(List<String> messages) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "code", HttpStatus.BAD_REQUEST.value(),
                        "messages", messages,
                        "timestamp", LocalDateTime.now()));
    }
}
