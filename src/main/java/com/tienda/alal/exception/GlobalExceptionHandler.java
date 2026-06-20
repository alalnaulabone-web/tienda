package com.tienda.alal.exception;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String extractDetail(Throwable e) {
        return Optional.ofNullable(e.getCause())
                .map(Throwable::getMessage)
                .orElse("");
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleFileUpload(FileUploadException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "error", e.getMessage(), "detail", extractDetail(e)));
    }

    @ExceptionHandler({FileNotFoundException.class, SpNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status", 404, "error", e.getMessage(), "detail", extractDetail(e)));
    }

    @ExceptionHandler(SpValidationException.class)
    public ResponseEntity<Map<String, Object>> handleSpValidation(SpValidationException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "error", e.getMessage(), "detail", extractDetail(e)));
    }

    @ExceptionHandler(SpExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleSpExecution(SpExecutionException e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("status", 500, "error", e.getMessage(), "detail", extractDetail(e)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("status", 500, "error", "Error interno del servidor", "detail", extractDetail(e)));
    }
}
