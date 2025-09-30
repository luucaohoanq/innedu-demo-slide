package com.innedu.slide.exception;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(String message) {
        super(message);
    }
    
    public TemplateNotFoundException(Long id) {
        super("Template not found with id: " + id);
    }
    
    public TemplateNotFoundException(String field, String value) {
        super("Template not found with " + field + ": " + value);
    }
}