package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CourseSection entity representing logical groupings of materials within a course.
 * Similar to chapters or modules in a course structure.
 * Maps to the 'course_sections' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for section metadata and material organization
 */
@Entity
@Table(name = "course_sections", schema = "etms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank(message = "Section title is required")
    @Size(min = 1, max = 255, message = "Section title must be between 1 and 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Section order is required")
    @Min(value = 0, message = "Section order must be non-negative")
    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder = 0;

    @Min(value = 0, message = "Duration must be non-negative")
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Bidirectional relationship with materials
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("materialOrder ASC")
    private List<Material> materials = new ArrayList<>();

    /**
     * Set creation and update timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        ZonedDateTime now = ZonedDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    /**
     * Update timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    /**
     * Add a material to this section
     * @param material the material to add
     */
    public void addMaterial(Material material) {
        materials.add(material);
        material.setSection(this);
        
        // Set material order if not set
        if (material.getMaterialOrder() == null) {
            material.setMaterialOrder(materials.size() - 1);
        }
    }

    /**
     * Remove a material from this section
     * @param material the material to remove
     */
    public void removeMaterial(Material material) {
        materials.remove(material);
        material.setSection(null);
    }

    /**
     * Get total number of materials in this section
     * @return count of active materials
     */
    public int getTotalMaterials() {
        return (int) materials.stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                .count();
    }

    /**
     * Get total duration in hours (for display)
     * @return duration in hours
     */
    public double getDurationHours() {
        return durationMinutes / 60.0;
    }

    /**
     * Get formatted duration string (e.g., "2h 30m")
     * @return formatted duration
     */
    public String getFormattedDuration() {
        if (durationMinutes == null || durationMinutes == 0) {
            return "0m";
        }
        
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        
        if (hours > 0 && minutes > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh", hours);
        } else {
            return String.format("%dm", minutes);
        }
    }
}

