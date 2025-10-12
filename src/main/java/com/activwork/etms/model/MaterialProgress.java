package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * MaterialProgress entity representing learner progress on individual materials.
 * Maps to the 'material_progress' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for granular material completion tracking
 * - Enables video bookmarking and detailed analytics
 */
@Entity
@Table(
    name = "material_progress", 
    schema = "etms",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment_material", columnNames = {"enrollment_id", "material_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Enrollment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @NotNull(message = "Material is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @DecimalMin(value = "0.0", message = "Completion percent must be non-negative")
    @DecimalMax(value = "100.0", message = "Completion percent must not exceed 100")
    @Column(name = "completion_percent", precision = 5, scale = 2)
    private BigDecimal completionPercent = BigDecimal.ZERO;

    @Min(value = 0, message = "Time spent must be non-negative")
    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes = 0;

    @Min(value = 0, message = "Last position must be non-negative")
    @Column(name = "last_position_seconds")
    private Integer lastPositionSeconds = 0;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

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
     * Mark material as completed
     */
    public void complete() {
        this.isCompleted = true;
        this.completionPercent = BigDecimal.valueOf(100);
        if (this.completedAt == null) {
            this.completedAt = ZonedDateTime.now();
        }
    }

    /**
     * Update video playback position (for resume feature)
     * @param positionInSeconds current playback position
     */
    public void updatePosition(Integer positionInSeconds) {
        this.lastPositionSeconds = positionInSeconds;
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * Update completion percentage
     * @param percent completion percentage (0-100)
     */
    public void updateCompletion(BigDecimal percent) {
        this.completionPercent = percent;
        if (percent.compareTo(BigDecimal.valueOf(100)) == 0) {
            complete();
        }
    }

    /**
     * Add time spent on material
     * @param minutes minutes to add
     */
    public void addTimeSpent(Integer minutes) {
        this.timeSpentMinutes += minutes;
    }

    /**
     * Check if material is completed
     * @return true if material is marked as completed
     */
    public boolean isCompleted() {
        return Boolean.TRUE.equals(this.isCompleted);
    }
}

