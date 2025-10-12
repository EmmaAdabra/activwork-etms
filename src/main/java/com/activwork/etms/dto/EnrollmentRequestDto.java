package com.activwork.etms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for enrollment requests.
 * Used when a learner wants to enroll in a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDto {

    @NotNull(message = "Learner ID is required")
    private UUID learnerId;

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    private String notes; // Optional learner notes
}

