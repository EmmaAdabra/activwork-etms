package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Course entity representing training courses in the ETMS system.
 * Maps to the 'courses' table in the 'etms' schema.
 * 
 * GRASP Patterns:
 * - Information Expert: Responsible for course-related data and business rules
 * - Creator: Creates and manages Material and LiveSession objects
 */
@Entity
@Table(name = "courses", schema = "etms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    @Column(length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Instructor is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CourseCategory category;

    @NotNull(message = "Difficulty level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 50)
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Max(value = 200, message = "Duration must not exceed 200 hours")
    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;

    @NotNull(message = "Max enrollments is required")
    @Min(value = 1, message = "Max enrollments must be at least 1")
    @Max(value = 100, message = "Max enrollments must not exceed 100")
    @Column(name = "max_enrollments", nullable = false)
    private Integer maxEnrollments;

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "video_preview_url", length = 500)
    private String videoPreviewUrl;

    // PostgreSQL array mapped to List<String>
    @Column(columnDefinition = "TEXT[]")
    private List<String> prerequisites = new ArrayList<>();

    @Column(name = "learning_objectives", columnDefinition = "TEXT[]")
    private List<String> learningObjectives = new ArrayList<>();

    @Column(columnDefinition = "TEXT[]")
    private List<String> tags = new ArrayList<>();

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    private ZonedDateTime endDate;

    @Column(name = "enrollment_deadline")
    private ZonedDateTime enrollmentDeadline;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "enrollment_count")
    private Integer enrollmentCount = 0;

    @DecimalMin(value = "0.0", message = "Average rating must be non-negative")
    @DecimalMax(value = "5.0", message = "Average rating must not exceed 5.0")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Min(value = 0, message = "Total ratings must be non-negative")
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    // Bidirectional relationships (optional, add if needed for navigation)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Material> materials = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiveSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

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
     * Publish the course
     */
    public void publish() {
        this.status = CourseStatus.PUBLISHED;
        if (this.publishedAt == null) {
            this.publishedAt = ZonedDateTime.now();
        }
    }

    /**
     * Archive the course
     */
    public void archive() {
        this.status = CourseStatus.ARCHIVED;
    }

    /**
     * Check if course is published and active
     * @return true if course is published and active
     */
    public boolean isAvailableForEnrollment() {
        return CourseStatus.PUBLISHED.equals(this.status) && 
               Boolean.TRUE.equals(this.isActive) &&
               this.enrollmentCount < this.maxEnrollments;
    }

    /**
     * Check if enrollment deadline has passed
     * @return true if enrollment deadline is in the past
     */
    public boolean isEnrollmentDeadlinePassed() {
        return this.enrollmentDeadline != null && 
               ZonedDateTime.now().isAfter(this.enrollmentDeadline);
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
}

