package com.activwork.etms.service;

import com.activwork.etms.dto.LoginDto;
import com.activwork.etms.dto.UserRegistrationDto;
import com.activwork.etms.dto.UserResponseDto;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.User;
import com.activwork.etms.model.UserRole;
import com.activwork.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for User entity operations.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for user-related business logic
 * - Handles authentication, registration, and profile management
 * - Contains all user-related validation and business rules
 * 
 * Architecture:
 * - Uses UserRepository for data access
 * - Throws ResourceNotFoundException when users not found
 * - Returns DTOs (never exposes entities directly)
 * - Uses @Transactional for data consistency
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Register a new user (instructor or learner).
     * 
     * @param registrationDto user registration data
     * @return created user response
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user with email: {}", registrationDto.getEmail());
        
        // Business rule: Email must be unique
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + registrationDto.getEmail());
        }
        
        // Create new user entity
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(registrationDto.getRole());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setDepartment(registrationDto.getDepartment());
        user.setPositionLevel(registrationDto.getPositionLevel());
        user.setIsActive(true);
        user.setIsVerified(false); // Email verification would be needed
        
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * Authenticate user with email and password.
     * 
     * @param loginDto login credentials
     * @return authenticated user response
     * @throws ResourceNotFoundException if user not found
     * @throws IllegalArgumentException if password is incorrect
     */
    @Transactional
    public UserResponseDto authenticateUser(LoginDto loginDto) {
        log.info("Authenticating user with email: {}", loginDto.getEmail());
        
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginDto.getEmail()));
        
        // Verify password
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Business rule: User must be active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("User account is inactive");
        }
        
        // Update last login
        user.setLastLogin(ZonedDateTime.now());
        userRepository.save(user);
        
        log.info("User authenticated successfully: {}", user.getId());
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Get user by ID.
     * 
     * @param userId the user UUID
     * @return user response
     * @throws ResourceNotFoundException if user not found
     */
    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Get user by email.
     * 
     * @param email the user email
     * @return user response
     * @throws ResourceNotFoundException if user not found
     */
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Get all users with a specific role.
     * 
     * @param role the user role (INSTRUCTOR or LEARNER)
     * @return list of users with the specified role
     */
    public List<UserResponseDto> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all instructors.
     * 
     * @return list of instructor users
     */
    public List<UserResponseDto> getAllInstructors() {
        return getUsersByRole(UserRole.INSTRUCTOR);
    }

    /**
     * Get all learners.
     * 
     * @return list of learner users
     */
    public List<UserResponseDto> getAllLearners() {
        return getUsersByRole(UserRole.LEARNER);
    }

    /**
     * Get all active users.
     * 
     * @return list of active users
     */
    public List<UserResponseDto> getActiveUsers() {
        List<User> users = userRepository.findByIsActive(true);
        return users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update user activity timestamp.
     * Called when user performs any action to track engagement.
     * 
     * @param userId the user UUID
     */
    @Transactional
    public void updateUserActivity(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        user.setLastActivity(ZonedDateTime.now());
        userRepository.save(user);
    }

    /**
     * Deactivate user account.
     * 
     * @param userId the user UUID
     */
    @Transactional
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("User deactivated: {}", userId);
    }

    /**
     * Activate user account.
     * 
     * @param userId the user UUID
     */
    @Transactional
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        user.setIsActive(true);
        userRepository.save(user);
        
        log.info("User activated: {}", userId);
    }

    /**
     * Check if user exists by email.
     * 
     * @param email the email to check
     * @return true if user exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

