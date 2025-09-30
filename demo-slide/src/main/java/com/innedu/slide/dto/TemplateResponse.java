package com.innedu.slide.dto;

import com.innedu.slide.entity.Template;

import java.time.LocalDateTime;

public class TemplateResponse {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String filePath;
    private String content;
    private String theme;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public TemplateResponse() {}

    // Constructor from Entity
    public TemplateResponse(Template template) {
        this.id = template.getId();
        this.name = template.getName();
        this.displayName = template.getDisplayName();
        this.description = template.getDescription();
        this.filePath = template.getFilePath();
        this.content = template.getContent();
        this.theme = template.getTheme();
        this.isActive = template.getIsActive();
        this.createdAt = template.getCreatedAt();
        this.updatedAt = template.getUpdatedAt();
    }

    // Static factory method
    public static TemplateResponse from(Template template) {
        return new TemplateResponse(template);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}