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
 * Enrollment entity representing a learner's enrollment in a course.
 * Maps to the 'enrollments' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for enrollment status and progress tracking
 * - Contains all enrollment-related data and completion information
 */
@Entity
@Table(
    name = "enrollments", 
    schema = "etms",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment_learner_course", columnNames = {"learner_id", "course_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Learner is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private ZonedDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @DecimalMin(value = "0.0", message = "Progress must be non-negative")
    @DecimalMax(value = "100.0", message = "Progress must not exceed 100")
    @Column(name = "progress_percent", precision = 5, scale = 2)
    private BigDecimal progressPercent = BigDecimal.ZERO;

    @Min(value = 0, message = "Completed materials must be non-negative")
    @Column(name = "completed_materials")
    private Integer completedMaterials = 0;

    @Min(value = 0, message = "Total materials must be non-negative")
    @Column(name = "total_materials")
    private Integer totalMaterials = 0;

    @Min(value = 0, message = "Time spent must be non-negative")
    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes = 0;

    @Column(name = "last_accessed")
    private ZonedDateTime lastAccessed;

    @Column(name = "completion_date")
    private ZonedDateTime completionDate;

    @Column(name = "certificate_issued")
    private Boolean certificateIssued = false;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Set enrollment timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        ZonedDateTime now = ZonedDateTime.now();
        if (enrolledAt == null) {
            enrolledAt = now;
        }
        if (lastAccessed == null) {
            lastAccessed = now;
        }
    }

    /**
     * Update last accessed timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        lastAccessed = ZonedDateTime.now();
    }

    /**
     * Check if enrollment is active
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return EnrollmentStatus.ACTIVE.equals(this.status);
    }

    /**
     * Check if enrollment is completed
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return EnrollmentStatus.COMPLETED.equals(this.status);
    }

    /**
     * Calculate progress percentage
     * @return progress as percentage (0-100)
     */
    public BigDecimal calculateProgress() {
        if (totalMaterials == null || totalMaterials == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completedMaterials)
            .divide(BigDecimal.valueOf(totalMaterials), 2, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Mark enrollment as completed
     */
    public void complete() {
        this.status = EnrollmentStatus.COMPLETED;
        this.progressPercent = BigDecimal.valueOf(100);
        if (this.completionDate == null) {
            this.completionDate = ZonedDateTime.now();
        }
    }

    /**
     * Cancel enrollment
     */
    public void cancel() {
        this.status = EnrollmentStatus.CANCELLED;
    }

    /**
     * Check if certificate can be issued
     * @return true if completed and certificate not yet issued
     */
    public boolean canIssueCertificate() {
        return isCompleted() && !Boolean.TRUE.equals(this.certificateIssued);
    }
}

