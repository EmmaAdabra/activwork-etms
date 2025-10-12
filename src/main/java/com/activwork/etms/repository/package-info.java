/**
 * Repository layer package containing Spring Data JPA repositories.
 * 
 * This package implements the Data Access Layer of the layered architecture,
 * providing clean abstraction for database operations.
 * 
 * Core Repositories (6):
 * - UserRepository: User management (instructors and learners)
 * - CourseRepository: Course CRUD and search operations
 * - MaterialRepository: Course materials management
 * - EnrollmentRepository: Enrollment tracking and progress
 * - FeedbackRepository: Course ratings and feedback
 * - LiveSessionRepository: Live training session management
 * 
 * Innovative Repositories (4):
 * - CoursePrerequisiteRepository: Learning path management
 * - MaterialProgressRepository: Granular progress tracking (video bookmarking)
 * - NotificationRepository: User notifications and alerts
 * - CourseAnalyticsRepository: Course performance metrics
 * 
 * Repository Design Principles:
 * 1. Keep repositories simple - just data access
 * 2. Return Optional<T> or List<T> - no exception throwing
 * 3. Let services handle business logic and exceptions
 * 4. Use Spring Data JPA naming conventions
 * 5. Add custom @Query for complex queries
 * 
 * Naming Conventions (Spring Data JPA):
 * - findBy[Property]: Find entities by property value
 * - findBy[Property]And[Property]: Multiple conditions with AND
 * - findBy[Property]Or[Property]: Multiple conditions with OR
 * - existsBy[Property]: Check if entity exists
 * - countBy[Property]: Count entities matching criteria
 * - deleteBy[Property]: Delete entities matching criteria
 * 
 * Custom Queries:
 * Use @Query annotation for complex queries that can't be expressed
 * with method naming conventions.
 * 
 * Architecture Pattern:
 * <pre>
 * Controller Layer
 *     ↓ calls
 * Service Layer
 *     ↓ calls
 * Repository Layer  ← THIS PACKAGE
 *     ↓ uses
 * Entity Layer
 *     ↓ maps to
 * Database (PostgreSQL)
 * </pre>
 * 
 * Usage Example:
 * <pre>
 * // Repository stays simple
 * public interface UserRepository extends JpaRepository<User, UUID> {
 *     Optional<User> findByEmail(String email);
 *     List<User> findByRole(UserRole role);
 *     boolean existsByEmail(String email);
 * }
 * 
 * // Service handles business logic
 * {@literal @}Service
 * public class UserService {
 *     
 *     public User getUserByEmail(String email) {
 *         return userRepository.findByEmail(email)
 *             .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
 *     }
 * }
 * </pre>
 * 
 * Benefits:
 * - Spring Data JPA generates implementations automatically
 * - Type-safe queries
 * - Easy to test with in-memory database
 * - Clean separation from business logic
 * - Standard Spring Boot pattern
 * 
 * Testing:
 * Repositories can be tested using @DataJpaTest annotation which provides
 * an in-memory database for fast testing.
 * 
 * @since 1.0.0
 */
package com.activwork.etms.repository;
