package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * User entity representing both instructors and learners in the ETMS system.
 * Maps to the 'users' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for user-related data and validation
 * - Contains all user profile information and authentication data
 */
@Entity
@Table(name = "users", schema = "etms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "user_role")
    private UserRole role;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String department;

    @Column(name = "position_level", length = 50)
    private String positionLevel;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Pattern(regexp = "^https://linkedin\\.com/in/.*", message = "Invalid LinkedIn URL")
    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Pattern(regexp = "^https://github\\.com/.*", message = "Invalid GitHub URL")
    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(length = 50)
    private String timezone = "UTC";

    @Column(name = "language_preference", length = 10)
    private String languagePreference = "en";

    @Column(name = "notification_preferences", columnDefinition = "jsonb")
    private String notificationPreferences = "{\"email\": true, \"push\": true, \"sms\": false}";

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @Column(name = "last_activity")
    private ZonedDateTime lastActivity;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private ZonedDateTime passwordResetExpires;

    /**
     * Set creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
        if (lastActivity == null) {
            lastActivity = ZonedDateTime.now();
        }
    }

    /**
     * Update last activity timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        lastActivity = ZonedDateTime.now();
    }

    /**
     * Check if user is an instructor
     * @return true if user role is INSTRUCTOR
     */
    public boolean isInstructor() {
        return UserRole.INSTRUCTOR.equals(this.role);
    }

    /**
     * Check if user is a learner
     * @return true if user role is LEARNER
     */
    public boolean isLearner() {
        return UserRole.LEARNER.equals(this.role);
    }

    /**
     * Get display name (for UI)
     * @return user's name
     */
    public String getDisplayName() {
        return this.name;
    }
}

