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

    private UUID courseId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(min = 20, message = "Comment must be at least 20 characters")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}

