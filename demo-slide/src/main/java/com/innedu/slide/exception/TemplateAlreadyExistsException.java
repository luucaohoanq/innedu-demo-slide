package com.innedu.slide.exception;

public class TemplateAlreadyExistsException extends RuntimeException {
    public TemplateAlreadyExistsException(String message) {
        super(message);
    }
    
    public static TemplateAlreadyExistsException forTemplateName(String templateName) {
        return new TemplateAlreadyExistsException("Template already exists with name: " + templateName);
    }
}