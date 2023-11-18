package wrzesniak.rafal.my.multimedia.manager.controller;

//@ControllerAdvice
public class ExceptionHandlerAdvice {

//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Map<String, Object>> handleValidationException(ConstraintViolationException e) {
//        List<String> messages = e.getConstraintViolations().stream()
//                .map(ConstraintViolation::getMessageTemplate)
//                .toList();
//
//        return buildResponseEntity(messages);
//    }
//
//    @ExceptionHandler(BasicApplicationException.class)
//    public ResponseEntity<Map<String, Object>> handleValidationException(BasicApplicationException e) {
//        return buildResponseEntity(List.of(e.getClass().getSimpleName()));
//    }
//
//    @ExceptionHandler(BindException.class)
//    public ResponseEntity<Map<String, Object>> handleValidationException(BindException e) {
//        List<String> errors = e.getAllErrors().stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .toList();
//
//        return buildResponseEntity(errors);
//    }
//
//    private ResponseEntity<Map<String, Object>> buildResponseEntity(List<String> messages) {
//        return ResponseEntity
//                .badRequest()
//                .body(Map.of(
//                        "status", HttpStatus.BAD_REQUEST.getReasonPhrase(),
//                        "code", HttpStatus.BAD_REQUEST.value(),
//                        "messages", messages,
//                        "timestamp", LocalDateTime.now()));
//    }
}
