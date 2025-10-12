package com.activwork.etms.dto;

import com.activwork.etms.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO for user response data.
 * Returns safe user information to clients (excludes sensitive data like password).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private String phoneNumber;
    private String department;
    private String positionLevel;
    private String profilePictureUrl;
    private String bio;
    private String linkedinUrl;
    private String githubUrl;
    private String timezone;
    private Boolean isActive;
    private Boolean isVerified;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastLogin;

    /**
     * Static factory method to create UserResponseDto from User entity.
     * Excludes sensitive information like passwordHash.
     */
    public static UserResponseDto fromEntity(com.activwork.etms.model.User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDepartment(user.getDepartment());
        dto.setPositionLevel(user.getPositionLevel());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setBio(user.getBio());
        dto.setLinkedinUrl(user.getLinkedinUrl());
        dto.setGithubUrl(user.getGithubUrl());
        dto.setTimezone(user.getTimezone());
        dto.setIsActive(user.getIsActive());
        dto.setIsVerified(user.getIsVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}

