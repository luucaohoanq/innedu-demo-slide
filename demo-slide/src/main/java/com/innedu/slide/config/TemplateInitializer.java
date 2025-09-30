package com.innedu.slide.config;

import com.innedu.slide.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TemplateInitializer implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateInitializer.class);
    
    private final TemplateService templateService;
    
    public TemplateInitializer(TemplateService templateService) {
        this.templateService = templateService;
    }
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Initializing templates from file system...");
        try {
            templateService.initializeTemplatesFromFileSystem();
            logger.info("Templates initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize templates", e);
        }
    }
}