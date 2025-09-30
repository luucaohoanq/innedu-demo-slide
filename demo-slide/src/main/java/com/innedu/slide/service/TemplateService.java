package com.innedu.slide.service;

import com.innedu.slide.dto.TemplateCreateRequest;
import com.innedu.slide.dto.TemplateUpdateRequest;
import com.innedu.slide.entity.Template;
import com.innedu.slide.exception.TemplateAlreadyExistsException;
import com.innedu.slide.exception.TemplateNotFoundException;
import com.innedu.slide.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@Transactional
public class TemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    
    private final TemplateRepository templateRepository;
    private final String templatesPath;
    
    public TemplateService(TemplateRepository templateRepository, 
                          @Value("${app.templates.path:../reveal.js/templates}") String templatesPath) {
        this.templateRepository = templateRepository;
        this.templatesPath = templatesPath;
    }
    
    /**
     * Get all templates
     */
    @Transactional(readOnly = true)
    public List<Template> getAllTemplates() {
        return templateRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get all templates paged
     */

    @Transactional(readOnly = true)
    public Page<Template> getAllTemplatesPaged(Pageable pageable) {
        return templateRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * Get all active templates
     */
    @Transactional(readOnly = true)
    public List<Template> getActiveTemplates() {
        return templateRepository.findByIsActiveTrue();
    }
    
    /**
     * Get template by ID
     */
    @Transactional(readOnly = true)
    public Template getTemplateById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));
    }
    
    /**
     * Get template by name
     */
    @Transactional(readOnly = true)
    public Template getTemplateByName(String name) {
        return templateRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new TemplateNotFoundException("name", name));
    }
    
    /**
     * Create a new template
     */
    public Template createTemplate(TemplateCreateRequest request) {
        // Check if template with the same name already exists
        if (templateRepository.existsByNameIgnoreCase(request.getName())) {
            throw TemplateAlreadyExistsException.forTemplateName(request.getName());
        }
        
        // Generate file path
        String fileName = request.getName().toLowerCase().replaceAll("[^a-z0-9]", "-") + ".html";
        String filePath = fileName;
        
        // Create template entity
        Template template = new Template();
        template.setName(request.getName());
        template.setDisplayName(request.getDisplayName());
        template.setDescription(request.getDescription());
        template.setFilePath(filePath);
        template.setContent(request.getContent());
        template.setTheme(request.getTheme() != null ? request.getTheme() : "black");
        template.setIsActive(true);
        
        // Save to database
        Template savedTemplate = templateRepository.save(template);
        
        // Save to file system
        try {
            saveTemplateToFile(savedTemplate);
            logger.info("Template created successfully: {} (ID: {})", savedTemplate.getName(), savedTemplate.getId());
        } catch (IOException e) {
            // If file creation fails, we should rollback the database transaction
            logger.error("Failed to create template file for: " + savedTemplate.getName(), e);
            throw new RuntimeException("Failed to create template file: " + e.getMessage(), e);
        }
        
        return savedTemplate;
    }
    
    /**
     * Update an existing template
     */
    public Template updateTemplate(Long id, TemplateUpdateRequest request) {
        Template template = getTemplateById(id);
        
        // Update template properties
        template.setDisplayName(request.getDisplayName());
        template.setDescription(request.getDescription());
        template.setContent(request.getContent());
        
        if (request.getTheme() != null) {
            template.setTheme(request.getTheme());
        }
        
        if (request.getIsActive() != null) {
            template.setIsActive(request.getIsActive());
        }
        
        // Save to database
        Template updatedTemplate = templateRepository.save(template);
        
        // Update file system
        try {
            saveTemplateToFile(updatedTemplate);
            logger.info("Template updated successfully: {} (ID: {})", updatedTemplate.getName(), updatedTemplate.getId());
        } catch (IOException e) {
            logger.error("Failed to update template file for: " + updatedTemplate.getName(), e);
            throw new RuntimeException("Failed to update template file: " + e.getMessage(), e);
        }
        
        return updatedTemplate;
    }
    
    /**
     * Delete a template
     */
    public void deleteTemplate(Long id) {
        Template template = getTemplateById(id);
        
        // Delete from file system first
        try {
            deleteTemplateFile(template);
        } catch (IOException e) {
            logger.warn("Failed to delete template file for: " + template.getName(), e);
            // Continue with database deletion even if file deletion fails
        }
        
        // Delete from database
        templateRepository.delete(template);
        logger.info("Template deleted successfully: {} (ID: {})", template.getName(), template.getId());
    }
    
    /**
     * Search templates
     */
    @Transactional(readOnly = true)
    public List<Template> searchTemplates(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllTemplates();
        }
        return templateRepository.findByDisplayNameContainingOrDescriptionContainingIgnoreCase(searchText.trim());
    }
    
    /**
     * Initialize templates from file system
     */
    @Transactional
    public void initializeTemplatesFromFileSystem() {
        try {
            Path templatesDir = Paths.get(templatesPath);
            if (!Files.exists(templatesDir)) {
                logger.warn("Templates directory does not exist: {}", templatesPath);
                return;
            }
            
            Files.list(templatesDir)
                    .filter(path -> path.toString().endsWith(".html"))
                    .forEach(this::loadTemplateFromFile);
                    
        } catch (IOException e) {
            logger.error("Failed to initialize templates from file system", e);
        }
    }
    
    /**
     * Load template from file system
     */
    private void loadTemplateFromFile(Path filePath) {
        try {
            String fileName = filePath.getFileName().toString();
            String templateName = fileName.replace(".html", "").replaceAll("-", " ");
            
            // Skip if already exists in database
            if (templateRepository.existsByNameIgnoreCase(templateName)) {
                return;
            }
            
            String content = Files.readString(filePath);
            
            Template template = new Template();
            template.setName(templateName);
            template.setDisplayName(capitalizeWords(templateName));
            template.setDescription("Loaded from existing file: " + fileName);
            template.setFilePath(fileName);
            template.setContent(content);
            template.setTheme(extractThemeFromContent(content));
            template.setIsActive(true);
            
            templateRepository.save(template);
            logger.info("Loaded template from file: {}", fileName);
            
        } catch (IOException e) {
            logger.error("Failed to load template from file: " + filePath, e);
        }
    }
    
    /**
     * Save template to file system
     */
    private void saveTemplateToFile(Template template) throws IOException {
        Path templatesDir = Paths.get(templatesPath);
        
        // Create directory if it doesn't exist
        if (!Files.exists(templatesDir)) {
            Files.createDirectories(templatesDir);
        }
        
        Path filePath = templatesDir.resolve(template.getFilePath());
        Files.writeString(filePath, template.getContent(), 
                         StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    /**
     * Delete template file from file system
     */
    private void deleteTemplateFile(Template template) throws IOException {
        Path filePath = Paths.get(templatesPath, template.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
    
    /**
     * Extract theme from template content
     */
    private String extractThemeFromContent(String content) {
        // Look for theme CSS link in the content
        if (content.contains("theme/white.css")) return "white";
        if (content.contains("theme/black.css")) return "black";
        if (content.contains("theme/league.css")) return "league";
        if (content.contains("theme/beige.css")) return "beige";
        if (content.contains("theme/sky.css")) return "sky";
        if (content.contains("theme/night.css")) return "night";
        if (content.contains("theme/serif.css")) return "serif";
        if (content.contains("theme/simple.css")) return "simple";
        if (content.contains("theme/solarized.css")) return "solarized";
        if (content.contains("theme/blood.css")) return "blood";
        if (content.contains("theme/moon.css")) return "moon";
        
        return "black"; // default
    }
    
    /**
     * Capitalize first letter of each word
     */
    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }
        
        return result.toString();
    }
}