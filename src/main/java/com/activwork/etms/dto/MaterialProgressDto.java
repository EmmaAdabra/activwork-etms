package com.activwork.etms.dto;

import com.activwork.etms.model.MaterialProgress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO for material progress response data.
 * Returns material progress information for learners.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialProgressDto {

    private UUID id;
    private UUID enrollmentId;
    private UUID materialId;
    
    private Boolean isCompleted;
    private Double completionPercent;
    private Integer timeSpentMinutes;
    private Integer lastPositionSeconds;
    
    private ZonedDateTime completedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    
    // private Boolean suspiciousActivity;

    /**
     * Static factory method to create MaterialProgressDto from MaterialProgress entity.
     */
    public static MaterialProgressDto fromEntity(MaterialProgress progress) {
        MaterialProgressDto dto = new MaterialProgressDto();
        dto.setId(progress.getId());
        dto.setEnrollmentId(progress.getEnrollment().getId());
        dto.setMaterialId(progress.getMaterial().getId());
        dto.setIsCompleted(progress.getIsCompleted());
        dto.setCompletionPercent(progress.getCompletionPercent().doubleValue());
        dto.setTimeSpentMinutes(progress.getTimeSpentMinutes());
        dto.setLastPositionSeconds(progress.getLastPositionSeconds());
        dto.setCompletedAt(progress.getCompletedAt());
        dto.setCreatedAt(progress.getCreatedAt());
        dto.setUpdatedAt(progress.getUpdatedAt());
        // dto.setSuspiciousActivity(progress.getSuspiciousActivity());
        return dto;
    }
}
