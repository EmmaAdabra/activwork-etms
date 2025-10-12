package com.activwork.etms.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for submitting course feedback.
 * Used by learners to rate and review courses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}

