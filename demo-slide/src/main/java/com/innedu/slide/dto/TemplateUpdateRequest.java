package com.innedu.slide.dto;

import jakarta.validation.constraints.NotBlank;

public class TemplateUpdateRequest {
    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;

    @NotBlank(message = "Template content is required")
    private String content;

    private String theme;

    private Boolean isActive;

    // Default constructor
    public TemplateUpdateRequest() {}

    // Constructor
    public TemplateUpdateRequest(String displayName, String description, String content, String theme, Boolean isActive) {
        this.displayName = displayName;
        this.description = description;
        this.content = content;
        this.theme = theme;
        this.isActive = isActive;
    }

    // Getters and Setters
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
}