package com.innedu.slide.dto;

import jakarta.validation.constraints.NotBlank;

public class TemplateCreateRequest {
    @NotBlank(message = "Template name is required")
    private String name;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;

    @NotBlank(message = "Template content is required")
    private String content;

    private String theme = "black";

    // Default constructor
    public TemplateCreateRequest() {}

    // Constructor
    public TemplateCreateRequest(String name, String displayName, String description, String content, String theme) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.content = content;
        this.theme = theme;
    }

    // Getters and Setters
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
}