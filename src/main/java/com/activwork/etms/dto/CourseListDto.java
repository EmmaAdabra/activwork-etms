package com.activwork.etms.dto;

import com.activwork.etms.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for course list/summary data.
 * Lighter version for displaying multiple courses (browse, search results).
 * Excludes heavy fields like full description.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseListDto {

    private UUID id;
    private String title;
    private String summary;
    
    private String instructorName;
    
    private CourseCategory category;
    private DifficultyLevel difficultyLevel;
    private Integer durationHours;
    private BigDecimal price;
    private CourseStatus status;
    
    private String thumbnailUrl;
    
    private Boolean isFeatured;
    
    // Quick stats
    private Integer enrollmentCount;
    private BigDecimal averageRating;
    private Integer totalRatings;

    /**
     * Static factory method to create CourseListDto from Course entity.
     * Returns lightweight course data suitable for list views.
     */
    public static CourseListDto fromEntity(Course course) {
        CourseListDto dto = new CourseListDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setSummary(course.getSummary());
        
        if (course.getInstructor() != null) {
            dto.setInstructorName(course.getInstructor().getName());
        }
        
        dto.setCategory(course.getCategory());
        dto.setDifficultyLevel(course.getDifficultyLevel());
        dto.setDurationHours(course.getDurationHours());
        dto.setPrice(course.getPrice());
        dto.setStatus(course.getStatus());
        
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setIsFeatured(course.getIsFeatured());
        
        dto.setEnrollmentCount(course.getEnrollmentCount());
        dto.setAverageRating(course.getAverageRating());
        dto.setTotalRatings(course.getTotalRatings());
        
        return dto;
    }
}

