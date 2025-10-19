package com.activwork.etms.dto;

import com.activwork.etms.model.CourseCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for creating new courses.
 * Used by instructors to create course drafts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateDto {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;

    private String description;

    @NotNull(message = "Category is required")
    private CourseCategory category;


    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Max(value = 200, message = "Duration must not exceed 200 hours")
    private Integer durationHours;


    private String thumbnailUrl;
    private String videoPreviewUrl;

    private List<String> prerequisites = new ArrayList<>();
    private List<String> learningObjectives = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private ZonedDateTime endDate;
}

