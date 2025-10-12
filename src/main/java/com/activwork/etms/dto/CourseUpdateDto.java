package com.activwork.etms.dto;

import com.activwork.etms.model.CourseCategory;
import com.activwork.etms.model.CourseStatus;
import com.activwork.etms.model.DifficultyLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO for updating existing courses.
 * Used by instructors to modify course details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateDto {

    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;

    private String description;

    private CourseCategory category;
    private DifficultyLevel difficultyLevel;

    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Max(value = 200, message = "Duration must not exceed 200 hours")
    private Integer durationHours;

    @Min(value = 1, message = "Max enrollments must be at least 1")
    @Max(value = 100, message = "Max enrollments must not exceed 100")
    private Integer maxEnrollments;

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    private CourseStatus status;
    private String thumbnailUrl;
    private String videoPreviewUrl;

    private List<String> prerequisites;
    private List<String> learningObjectives;
    private List<String> tags;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime enrollmentDeadline;

    private Boolean isFeatured;
    private Boolean isActive;
}

