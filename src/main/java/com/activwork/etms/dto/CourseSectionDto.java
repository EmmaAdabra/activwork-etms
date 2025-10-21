package com.activwork.etms.dto;

import com.activwork.etms.model.CourseSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for CourseSection entity.
 * Used for transferring section data between layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionDto {
    
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer sectionOrder;
    private Integer durationMinutes;
    private Boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    
    // Materials in this section
    private List<MaterialResponseDto> materials = new ArrayList<>();
    
    // Progress tracking fields (for learner view)
    private Integer totalMaterials;
    private Integer completedMaterials;
    private Double completionPercent;
    
    /**
     * Convert CourseSection entity to DTO
     * @param section the course section entity
     * @return CourseSectionDto
     */
    public static CourseSectionDto fromEntity(CourseSection section) {
        CourseSectionDto dto = new CourseSectionDto();
        dto.setId(section.getId());
        dto.setCourseId(section.getCourse().getId());
        dto.setTitle(section.getTitle());
        dto.setDescription(section.getDescription());
        dto.setSectionOrder(section.getSectionOrder());
        dto.setDurationMinutes(section.getDurationMinutes());
        dto.setIsActive(section.getIsActive());
        dto.setCreatedAt(section.getCreatedAt());
        dto.setUpdatedAt(section.getUpdatedAt());
        
        // Convert materials if loaded
        if (section.getMaterials() != null && !section.getMaterials().isEmpty()) {
            dto.setMaterials(section.getMaterials().stream()
                    .filter(m -> Boolean.TRUE.equals(m.getIsActive())) // Only include active materials
                    .map(MaterialResponseDto::fromEntity)
                    .collect(Collectors.toList()));
            System.out.println("✅ Section '" + section.getTitle() + "' has " + dto.getMaterials().size() + " active materials");
        } else {
            System.out.println("⚠️ Section '" + section.getTitle() + "' has NO materials (null or empty)");
        }
        
        // Calculate total materials
        dto.setTotalMaterials(section.getTotalMaterials());
        
        return dto;
    }
    
    /**
     * Convert CourseSection entity to DTO with progress information
     * @param section the course section entity
     * @param completedMaterials number of completed materials
     * @return CourseSectionDto with progress
     */
    public static CourseSectionDto fromEntityWithProgress(CourseSection section, int completedMaterials) {
        CourseSectionDto dto = fromEntity(section);
        dto.setCompletedMaterials(completedMaterials);
        
        // Calculate completion percentage
        if (dto.getTotalMaterials() != null && dto.getTotalMaterials() > 0) {
            dto.setCompletionPercent((double) completedMaterials / dto.getTotalMaterials() * 100);
        } else {
            dto.setCompletionPercent(0.0);
        }
        
        return dto;
    }
    
    /**
     * Get formatted duration (e.g., "2h 30m")
     * @return formatted duration string
     */
    public String getFormattedDuration() {
        if (durationMinutes == null || durationMinutes == 0) {
            return "0m";
        }
        
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        
        if (hours > 0 && minutes > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh", hours);
        } else {
            return String.format("%dm", minutes);
        }
    }
}

