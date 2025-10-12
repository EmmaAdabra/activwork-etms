package com.activwork.etms.repository;

import com.activwork.etms.model.User;
import com.activwork.etms.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity operations.
 * 
 * Provides data access methods for users (instructors and learners).
 * Spring Data JPA automatically generates implementations for these methods.
 * 
 * Architecture Note:
 * - Repositories return Optional or List - no exception throwing
 * - Services handle Optional.empty() and convert to domain exceptions
 * - Keeps data access layer clean and simple
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email address.
     * Used for authentication and duplicate email checking.
     * 
     * @param email the user's email address
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users with a specific role.
     * 
     * @param role the user role (INSTRUCTOR or LEARNER)
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find all active users.
     * 
     * @param isActive true to find active users, false for inactive
     * @return list of users matching the active status
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Check if a user exists with the given email.
     * Useful for checking duplicates before creating new users.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all verified users.
     * 
     * @param isVerified true to find verified users, false for unverified
     * @return list of users matching the verification status
     */
    List<User> findByIsVerified(Boolean isVerified);
}

