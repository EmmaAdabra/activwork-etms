package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Notification entity representing user notifications and alerts.
 * Maps to the 'notifications' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for notification data and delivery status
 * - Enables real-time user engagement
 */
@Entity
@Table(name = "notifications", schema = "etms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Message is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @NotBlank(message = "Type is required")
    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    /**
     * Set creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
    }

    /**
     * Mark notification as read
     */
    public void markAsRead() {
        this.isRead = true;
        if (this.readAt == null) {
            this.readAt = ZonedDateTime.now();
        }
    }

    /**
     * Mark notification as unread
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * Check if notification is unread
     * @return true if notification hasn't been read
     */
    public boolean isUnread() {
        return !Boolean.TRUE.equals(this.isRead);
    }

    /**
     * Check if notification has an action URL
     * @return true if actionUrl is not null
     */
    public boolean hasActionUrl() {
        return this.actionUrl != null && !this.actionUrl.trim().isEmpty();
    }

    /**
     * Notification type constants
     */
    public static class Type {
        public static final String COURSE_ENROLLMENT = "COURSE_ENROLLMENT";
        public static final String COURSE_UPDATE = "COURSE_UPDATE";
        public static final String SESSION_REMINDER = "SESSION_REMINDER";
        public static final String CERTIFICATE_READY = "CERTIFICATE_READY";
        public static final String INSTRUCTOR_NOTIFICATION = "INSTRUCTOR_NOTIFICATION";
        public static final String FEEDBACK_RECEIVED = "FEEDBACK_RECEIVED";
        public static final String MATERIAL_ADDED = "MATERIAL_ADDED";
        public static final String ENROLLMENT_COMPLETED = "ENROLLMENT_COMPLETED";
    }
}

