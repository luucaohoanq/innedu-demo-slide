package com.innedu.slide.controller;

import com.innedu.slide.dto.TemplateCreateRequest;
import com.innedu.slide.dto.TemplateResponse;
import com.innedu.slide.dto.TemplateUpdateRequest;
import com.innedu.slide.entity.Template;
import com.innedu.slide.exception.TemplateAlreadyExistsException;
import com.innedu.slide.exception.TemplateNotFoundException;
import com.innedu.slide.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
@Tag(name = "templates", description = "Template API")
@CrossOrigin(origins = "*") // Allow frontend to access the API
public class TemplateController {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    
    private final TemplateService templateService;
    
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }
    
    /**
     * Get all templates
     */
    @GetMapping
    @Operation(
        summary = "Get all templates",
        description = "Fetches all templates from the system.",
        method = "GET"
    )
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        try {
            List<Template> templates = templateService.getAllTemplates();
            List<TemplateResponse> response = templates.stream()
                    .map(TemplateResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching all templates", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<TemplateResponse>> getAllTemplatesPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id,asc") String sort) {
        try {
            // Parse sort string: e.g. "id,asc"
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

            Page<Template> templates = templateService.getAllTemplatesPaged(pageable);
            Page<TemplateResponse> response = templates.map(TemplateResponse::from);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching paged templates", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get active templates only
     */
    @GetMapping("/active")
    public ResponseEntity<List<TemplateResponse>> getActiveTemplates() {
        try {
            List<Template> templates = templateService.getActiveTemplates();
            List<TemplateResponse> response = templates.stream()
                    .map(TemplateResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching active templates", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get template by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplateById(@PathVariable Long id) {
        try {
            Template template = templateService.getTemplateById(id);
            return ResponseEntity.ok(TemplateResponse.from(template));
        } catch (TemplateNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching template with id: " + id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get template by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<TemplateResponse> getTemplateByName(@PathVariable String name) {
        try {
            Template template = templateService.getTemplateByName(name);
            return ResponseEntity.ok(TemplateResponse.from(template));
        } catch (TemplateNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching template with name: " + name, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search templates
     */
    @GetMapping("/search")
    public ResponseEntity<List<TemplateResponse>> searchTemplates(@RequestParam(required = false) String q) {
        try {
            List<Template> templates = templateService.searchTemplates(q);
            List<TemplateResponse> response = templates.stream()
                    .map(TemplateResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching templates with query: " + q, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Create a new template
     */
    @PostMapping
    public ResponseEntity<Object> createTemplate(@Valid @RequestBody TemplateCreateRequest request, 
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed");
            errorResponse.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            Template template = templateService.createTemplate(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(TemplateResponse.from(template));
        } catch (TemplateAlreadyExistsException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "TEMPLATE_ALREADY_EXISTS");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error creating template", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create template: " + e.getMessage());
            errorResponse.put("error", "CREATION_FAILED");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Update an existing template
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTemplate(@PathVariable Long id, 
                                               @Valid @RequestBody TemplateUpdateRequest request,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed");
            errorResponse.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            Template template = templateService.updateTemplate(id, request);
            return ResponseEntity.ok(TemplateResponse.from(template));
        } catch (TemplateNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "TEMPLATE_NOT_FOUND");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating template with id: " + id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update template: " + e.getMessage());
            errorResponse.put("error", "UPDATE_FAILED");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Delete a template
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Template deleted successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (TemplateNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "TEMPLATE_NOT_FOUND");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting template with id: " + id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to delete template: " + e.getMessage());
            errorResponse.put("error", "DELETION_FAILED");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Initialize templates from file system
     */
    @PostMapping("/initialize")
    public ResponseEntity<Object> initializeTemplates() {
        try {
            templateService.initializeTemplatesFromFileSystem();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Templates initialized successfully from file system");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error initializing templates", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to initialize templates: " + e.getMessage());
            errorResponse.put("error", "INITIALIZATION_FAILED");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}