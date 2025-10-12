package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * LiveSession entity representing scheduled live training sessions.
 * Maps to the 'live_sessions' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for session scheduling and meeting information
 */
@Entity
@Table(name = "live_sessions", schema = "etms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start time is required")
    @Column(name = "starts_at", nullable = false)
    private ZonedDateTime startsAt;

    @NotNull(message = "End time is required")
    @Column(name = "ends_at", nullable = false)
    private ZonedDateTime endsAt;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 8 hours (480 minutes)")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "session_status")
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    @Column(name = "meeting_id", length = 100)
    private String meetingId;

    @Column(name = "meeting_password", length = 50)
    private String meetingPassword;

    @Min(value = 1, message = "Max participants must be at least 1")
    @Column(name = "max_participants")
    private Integer maxParticipants = 100;

    @Column(name = "recording_url", length = 500)
    private String recordingUrl;

    @Column(name = "recording_available")
    private Boolean recordingAvailable = false;

    @Min(value = 0, message = "Attendance count must be non-negative")
    @Column(name = "attendance_count")
    private Integer attendanceCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    /**
     * Set creation and update timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        ZonedDateTime now = ZonedDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    /**
     * Update timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    /**
     * Start the session (change status to LIVE)
     */
    public void start() {
        this.status = SessionStatus.LIVE;
    }

    /**
     * Complete the session
     */
    public void complete() {
        this.status = SessionStatus.COMPLETED;
    }

    /**
     * Cancel the session
     */
    public void cancel() {
        this.status = SessionStatus.CANCELLED;
    }

    /**
     * Check if session is in the future
     * @return true if session hasn't started yet
     */
    public boolean isUpcoming() {
        return SessionStatus.SCHEDULED.equals(this.status) && 
               ZonedDateTime.now().isBefore(this.startsAt);
    }

    /**
     * Check if session is currently live
     * @return true if session is in progress
     */
    public boolean isLive() {
        return SessionStatus.LIVE.equals(this.status);
    }

    /**
     * Check if session is completed
     * @return true if session has ended
     */
    public boolean isCompleted() {
        return SessionStatus.COMPLETED.equals(this.status);
    }

    /**
     * Increment attendance count
     */
    public void incrementAttendance() {
        if (this.attendanceCount < this.maxParticipants) {
            this.attendanceCount++;
        }
    }
}

