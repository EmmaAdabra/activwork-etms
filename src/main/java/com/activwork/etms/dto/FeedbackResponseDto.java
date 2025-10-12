package com.activwork.etms.dto;

import com.activwork.etms.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO for feedback response data.
 * Returns feedback information with learner details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {

    private UUID id;
    
    private UUID learnerId;
    private String learnerName;
    
    private UUID courseId;
    
    private Integer rating;
    private String comment;
    
    private ZonedDateTime createdAt;
    private Boolean isVisible;

    /**
     * Static factory method to create FeedbackResponseDto from Feedback entity.
     */
    public static FeedbackResponseDto fromEntity(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(feedback.getId());
        
        if (feedback.getLearner() != null) {
            dto.setLearnerId(feedback.getLearner().getId());
            dto.setLearnerName(feedback.getLearner().getName());
        }
        
        if (feedback.getCourse() != null) {
            dto.setCourseId(feedback.getCourse().getId());
        }
        
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setIsVisible(feedback.getIsVisible());
        
        return dto;
    }
}

