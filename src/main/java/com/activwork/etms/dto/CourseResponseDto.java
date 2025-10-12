package com.activwork.etms.dto;

import com.activwork.etms.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for detailed course response data.
 * Returns complete course information including instructor details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {

    private UUID id;
    private String title;
    private String summary;
    private String description;
    
    // Instructor info (simplified)
    private UUID instructorId;
    private String instructorName;
    
    private CourseCategory category;
    private DifficultyLevel difficultyLevel;
    private Integer durationHours;
    private Integer maxEnrollments;
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
    
    // Analytics
    private Integer viewCount;
    private Integer enrollmentCount;
    private BigDecimal averageRating;
    private Integer totalRatings;
    
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime publishedAt;

    /**
     * Static factory method to create CourseResponseDto from Course entity.
     */
    public static CourseResponseDto fromEntity(Course course) {
        CourseResponseDto dto = new CourseResponseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setSummary(course.getSummary());
        dto.setDescription(course.getDescription());
        
        // Instructor info
        if (course.getInstructor() != null) {
            dto.setInstructorId(course.getInstructor().getId());
            dto.setInstructorName(course.getInstructor().getName());
        }
        
        dto.setCategory(course.getCategory());
        dto.setDifficultyLevel(course.getDifficultyLevel());
        dto.setDurationHours(course.getDurationHours());
        dto.setMaxEnrollments(course.getMaxEnrollments());
        dto.setPrice(course.getPrice());
        dto.setStatus(course.getStatus());
        
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setVideoPreviewUrl(course.getVideoPreviewUrl());
        
        dto.setPrerequisites(course.getPrerequisites());
        dto.setLearningObjectives(course.getLearningObjectives());
        dto.setTags(course.getTags());
        
        dto.setStartDate(course.getStartDate());
        dto.setEndDate(course.getEndDate());
        dto.setEnrollmentDeadline(course.getEnrollmentDeadline());
        
        dto.setIsFeatured(course.getIsFeatured());
        dto.setIsActive(course.getIsActive());
        
        dto.setViewCount(course.getViewCount());
        dto.setEnrollmentCount(course.getEnrollmentCount());
        dto.setAverageRating(course.getAverageRating());
        dto.setTotalRatings(course.getTotalRatings());
        
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setPublishedAt(course.getPublishedAt());
        
        return dto;
    }
}

