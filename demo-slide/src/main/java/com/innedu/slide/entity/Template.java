package com.innedu.slide.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "Template name is required")
    private String name;

    @Column(name = "display_name", nullable = false)
    @NotBlank(message = "Display name is required")
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "file_path", nullable = false)
    @NotBlank(message = "File path is required")
    private String filePath;

    @Column(name = "content", columnDefinition = "TEXT")
    @NotBlank(message = "Template content is required")
    private String content;

    @Column(name = "theme")
    private String theme = "black";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor with required fields
    public Template(String name, String displayName, String filePath, String content) {
        this.name = name;
        this.displayName = displayName;
        this.filePath = filePath;
        this.content = content;
    }
}