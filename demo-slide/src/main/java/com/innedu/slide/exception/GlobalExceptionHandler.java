package com.innedu.slide.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTemplateNotFound(TemplateNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("error", "TEMPLATE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(TemplateAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleTemplateAlreadyExists(TemplateAlreadyExistsException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("error", "TEMPLATE_ALREADY_EXISTS");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("error", "VALIDATION_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        logger.error("Unexpected error occurred", e);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "An unexpected error occurred: " + e.getMessage());
        response.put("error", "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred", e);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "An unexpected error occurred");
        response.put("error", "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}