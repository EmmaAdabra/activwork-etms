package com.activwork.etms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for material progress update requests.
 * Used when learners update their progress on materials.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialProgressUpdateDto {

    private UUID materialId;
    private Integer lastPositionSeconds;
    private Double completionPercent;
    private Integer timeSpentMinutes;
    private Boolean isCompleted;
    // private Boolean suspiciousActivity;
}
