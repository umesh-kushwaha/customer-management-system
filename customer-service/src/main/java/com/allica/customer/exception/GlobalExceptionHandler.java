package com.allica.customer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Handle @Valid validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", fieldErrors);
    }

    // 2. Handle Malformed JSON (syntax errors)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonErrors(HttpMessageNotReadableException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
    }


    // Handle ResponseStatusException (use status from exception)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return buildErrorResponse(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason() != null ? ex.getReason() : ex.getMessage(),
                null
        );
    }

    // 1. Handle Database Constraint Violations (e.g., Unique Constraints)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConstraints(DataIntegrityViolationException ex) {
        // Architect Note: Do not return ex.getMessage() as it contains SQL
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Database integrity violation: The record may already exist.",
                null
        );
    }

    // Catch-all for other unexpected errors (500 Internal Server Error)
    // 2. The "Safety Net" - Catch all other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        // Log the actual error for debugging, but hide it from the user
         log.error("Unhandled exception occurred: ", ex);

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            HttpStatus status = responseStatus.value();
            String reason = responseStatus.reason();
            return buildErrorResponse(
                    status,
                    reason != null && !reason.isBlank() ? reason : ex.getMessage(),
                    null
            );
        }

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                null
        );
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, Map<String, String> errors) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        if (errors != null)
            body.put("errors", errors);

        return new ResponseEntity<>(body, status);
    }
}
