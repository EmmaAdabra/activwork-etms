package com.activwork.etms.dto;

import com.activwork.etms.model.LiveSession;
import com.activwork.etms.model.SessionStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO for live session data (both request and response).
 * Used for creating/updating sessions and displaying session information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveSessionDto {

    private UUID id;
    
    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    private String description;

    @NotNull(message = "Start time is required")
    private ZonedDateTime startsAt;

    @NotNull(message = "End time is required")
    private ZonedDateTime endsAt;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 8 hours (480 minutes)")
    private Integer durationMinutes;

    private SessionStatus status;
    
    private String meetingLink;
    private String meetingId;
    private String meetingPassword;
    
    private Integer maxParticipants;
    private String recordingUrl;
    private Boolean recordingAvailable;
    private Integer attendanceCount;

    /**
     * Static factory method to create LiveSessionDto from LiveSession entity.
     */
    public static LiveSessionDto fromEntity(LiveSession session) {
        LiveSessionDto dto = new LiveSessionDto();
        dto.setId(session.getId());
        
        if (session.getCourse() != null) {
            dto.setCourseId(session.getCourse().getId());
        }
        
        dto.setTitle(session.getTitle());
        dto.setDescription(session.getDescription());
        dto.setStartsAt(session.getStartsAt());
        dto.setEndsAt(session.getEndsAt());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setStatus(session.getStatus());
        
        dto.setMeetingLink(session.getMeetingLink());
        dto.setMeetingId(session.getMeetingId());
        dto.setMeetingPassword(session.getMeetingPassword());
        
        dto.setMaxParticipants(session.getMaxParticipants());
        dto.setRecordingUrl(session.getRecordingUrl());
        dto.setRecordingAvailable(session.getRecordingAvailable());
        dto.setAttendanceCount(session.getAttendanceCount());
        
        return dto;
    }
}

