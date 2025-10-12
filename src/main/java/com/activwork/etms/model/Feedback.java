package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Feedback entity representing learner feedback and ratings for courses.
 * Maps to the 'feedback' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for feedback data and rating information
 */
@Entity
@Table(
    name = "feedback", 
    schema = "etms",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_feedback_learner_course", columnNames = {"learner_id", "course_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

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

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "is_visible")
    private Boolean isVisible = true;

    /**
     * Set creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
    }

    /**
     * Hide feedback (for moderation)
     */
    public void hide() {
        this.isVisible = false;
    }

    /**
     * Show feedback
     */
    public void show() {
        this.isVisible = true;
    }

    /**
     * Check if feedback has a comment
     * @return true if comment is not empty
     */
    public boolean hasComment() {
        return this.comment != null && !this.comment.trim().isEmpty();
    }

    /**
     * Get star rating as string (for display)
     * @return star representation (e.g., "★★★★☆")
     */
    public String getStarRating() {
        return "★".repeat(this.rating) + "☆".repeat(5 - this.rating);
    }
}

