/**
 * Data Transfer Objects (DTOs) package for the ETMS application.
 * 
 * This package contains DTOs that act as a contract between layers,
 * ensuring clean separation between the domain model and external interfaces.
 * 
 * Why DTOs?
 * 1. Decouple API from database entities
 * 2. Hide sensitive information (e.g., password_hash)
 * 3. Control exactly what data is exposed
 * 4. Allow different representations for different use cases
 * 5. Prevent accidental exposure of lazy-loaded relationships
 * 6. Enable API versioning without changing entities
 * 
 * DTO Categories:
 * 
 * Request DTOs (Input):
 * - LoginDto: User authentication
 * - UserRegistrationDto: New user registration
 * - CourseCreateDto: Creating new courses
 * - CourseUpdateDto: Updating courses
 * - EnrollmentRequestDto: Course enrollment
 * - FeedbackDto: Submitting course feedback
 * - LiveSessionDto: Creating/updating sessions
 * 
 * Response DTOs (Output):
 * - UserResponseDto: User information (excludes password)
 * - CourseResponseDto: Detailed course information
 * - CourseListDto: Lightweight course summary (for lists)
 * - EnrollmentResponseDto: Enrollment with progress
 * - FeedbackResponseDto: Feedback with learner info
 * - MaterialResponseDto: Material metadata
 * 
 * Design Patterns:
 * - Static factory methods (fromEntity) for entity-to-DTO conversion
 * - Validation annotations on request DTOs
 * - Immutable-style (Lombok @Data)
 * - No business logic (just data containers)
 * 
 * Usage Flow:
 * <pre>
 * Controller receives: CourseCreateDto (request)
 *     ↓ passes to
 * Service converts: CourseCreateDto → Course entity
 *     ↓ saves to database
 * Service converts: Course entity → CourseResponseDto
 *     ↓ returns
 * Controller sends: CourseResponseDto as JSON (response)
 * </pre>
 * 
 * Benefits:
 * - Clean API contracts
 * - Type-safe data transfer
 * - Automatic validation with Bean Validation
 * - Easy to test
 * - Frontend-friendly JSON structure
 * 
 * Example:
 * <pre>
 * // Entity (internal, database)
 * User user = new User();
 * user.setPasswordHash("$2a$10$...");  // Sensitive!
 * 
 * // DTO (external, API)
 * UserResponseDto dto = UserResponseDto.fromEntity(user);
 * // dto.getPassword() doesn't exist - password is hidden!
 * </pre>
 * 
 * @since 1.0.0
 */
package com.activwork.etms.dto;
