package com.activwork.etms.dto;

import com.activwork.etms.model.Enrollment;
import com.activwork.etms.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO for enrollment response data.
 * Returns enrollment information with progress tracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDto {

    private UUID id;
    
    // Course info
    private UUID courseId;
    private String courseTitle;
    private String courseThumbnailUrl;
    
    // Learner info
    private UUID learnerId;
    private String learnerName;
    
    private ZonedDateTime enrolledAt;
    private EnrollmentStatus status;
    
    // Progress tracking
    private BigDecimal progressPercent;
    private Integer completedMaterials;
    private Integer totalMaterials;
    private Integer timeSpentMinutes;
    
    private ZonedDateTime lastAccessed;
    private ZonedDateTime completionDate;
    
    private Boolean certificateIssued;
    private String certificateUrl;
    
    private String notes;

    /**
     * Static factory method to create EnrollmentResponseDto from Enrollment entity.
     */
    public static EnrollmentResponseDto fromEntity(Enrollment enrollment) {
        EnrollmentResponseDto dto = new EnrollmentResponseDto();
        dto.setId(enrollment.getId());
        
        // Course info
        if (enrollment.getCourse() != null) {
            dto.setCourseId(enrollment.getCourse().getId());
            dto.setCourseTitle(enrollment.getCourse().getTitle());
            dto.setCourseThumbnailUrl(enrollment.getCourse().getThumbnailUrl());
        }
        
        // Learner info
        if (enrollment.getLearner() != null) {
            dto.setLearnerId(enrollment.getLearner().getId());
            dto.setLearnerName(enrollment.getLearner().getName());
        }
        
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setStatus(enrollment.getStatus());
        
        dto.setProgressPercent(enrollment.getProgressPercent());
        dto.setCompletedMaterials(enrollment.getCompletedMaterials());
        dto.setTotalMaterials(enrollment.getTotalMaterials());
        dto.setTimeSpentMinutes(enrollment.getTimeSpentMinutes());
        
        dto.setLastAccessed(enrollment.getLastAccessed());
        dto.setCompletionDate(enrollment.getCompletionDate());
        
        dto.setCertificateIssued(enrollment.getCertificateIssued());
        dto.setCertificateUrl(enrollment.getCertificateUrl());
        
        dto.setNotes(enrollment.getNotes());
        
        return dto;
    }
}

