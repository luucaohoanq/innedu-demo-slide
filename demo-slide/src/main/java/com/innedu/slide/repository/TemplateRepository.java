package com.innedu.slide.repository;

import com.innedu.slide.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    
    /**
     * Find template by name (case-insensitive)
     */
    Optional<Template> findByNameIgnoreCase(String name);
    
    /**
     * Find all active templates
     */
    List<Template> findByIsActiveTrue();
    
    /**
     * Find all templates ordered by creation date
     */
    List<Template> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find templates by theme
     */
    List<Template> findByThemeAndIsActiveTrue(String theme);
    
    /**
     * Check if template exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find templates by display name containing text (case-insensitive)
     */
    @Query("SELECT t FROM Template t WHERE LOWER(t.displayName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Template> findByDisplayNameContainingOrDescriptionContainingIgnoreCase(String searchText);

    Page<Template> findAllByOrderByCreatedAtDesc(Pageable pageable);
}