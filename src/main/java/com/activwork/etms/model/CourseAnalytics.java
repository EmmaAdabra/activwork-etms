package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * CourseAnalytics entity representing daily course performance metrics.
 * Maps to the 'course_analytics' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for course analytics data and metrics
 * - Enables instructor dashboards and performance tracking
 */
@Entity
@Table(
    name = "course_analytics", 
    schema = "etms",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_date", columnNames = {"course_id", "date"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @Min(value = 0, message = "Views must be non-negative")
    @Column
    private Integer views = 0;

    @Min(value = 0, message = "Enrollments must be non-negative")
    @Column
    private Integer enrollments = 0;

    @Min(value = 0, message = "Completions must be non-negative")
    @Column
    private Integer completions = 0;

    @DecimalMin(value = "0.0", message = "Average rating must be non-negative")
    @DecimalMax(value = "5.0", message = "Average rating must not exceed 5.0")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Min(value = 0, message = "Total ratings must be non-negative")
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @DecimalMin(value = "0.0", message = "Revenue must be non-negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Set creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
        if (date == null) {
            date = LocalDate.now();
        }
    }

    /**
     * Increment view count
     */
    public void incrementViews() {
        this.views++;
    }

    /**
     * Increment enrollment count
     */
    public void incrementEnrollments() {
        this.enrollments++;
    }

    /**
     * Increment completion count
     */
    public void incrementCompletions() {
        this.completions++;
    }

    /**
     * Add revenue
     * @param amount revenue amount to add
     */
    public void addRevenue(BigDecimal amount) {
        this.revenue = this.revenue.add(amount);
    }

    /**
     * Update rating statistics
     * @param avgRating new average rating
     * @param totalCount total rating count
     */
    public void updateRatings(BigDecimal avgRating, Integer totalCount) {
        this.averageRating = avgRating;
        this.totalRatings = totalCount;
    }

    /**
     * Calculate conversion rate (enrollments/views)
     * @return conversion rate as percentage
     */
    public BigDecimal getConversionRate() {
        if (views == null || views == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(enrollments)
            .divide(BigDecimal.valueOf(views), 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate completion rate (completions/enrollments)
     * @return completion rate as percentage
     */
    public BigDecimal getCompletionRate() {
        if (enrollments == null || enrollments == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completions)
            .divide(BigDecimal.valueOf(enrollments), 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }
}

