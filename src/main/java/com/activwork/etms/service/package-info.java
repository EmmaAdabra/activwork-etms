/**
 * Service layer package containing business logic for the ETMS application.
 * 
 * This package implements the Business Logic Layer of the layered architecture,
 * sitting between controllers and repositories.
 * 
 * Core Services:
 * - UserService: User management, authentication, registration
 * - CourseService: Course lifecycle, CRUD, publishing (GRASP: Information Expert)
 * - EnrollmentService: Enrollment rules, progress tracking (complex business logic)
 * - FeedbackService: Rating submission, moderation
 * - MaterialService: Material management, file tracking
 * 
 * Service Layer Responsibilities:
 * 1. Business logic and validation
 * 2. Transaction management
 * 3. Convert between entities and DTOs
 * 4. Throw domain-specific exceptions
 * 5. Coordinate multiple repositories
 * 6. Implement GRASP patterns
 * 
 * GRASP Patterns Demonstrated:
 * 
 * 1. Information Expert:
 *    - CourseService is the expert on course business rules
 *    - EnrollmentService is the expert on enrollment validation
 *    - Each service has the information needed to fulfill its responsibilities
 * 
 * 2. Creator:
 *    - UserService creates User entities from registration data
 *    - CourseService creates Course entities from DTOs
 *    - Services are responsible for object creation with proper initialization
 * 
 * 3. Controller (implicit):
 *    - Services coordinate repository operations
 *    - Delegate data access to repositories
 *    - Control the flow of business operations
 * 
 * Architecture Pattern:
 * <pre>
 * Controller Layer
 *     ↓ calls service methods with DTOs
 * Service Layer  ← THIS PACKAGE
 *     ↓ validates business rules
 *     ↓ converts DTOs to entities
 *     ↓ calls repositories
 * Repository Layer
 *     ↓ performs database operations
 * Database
 * </pre>
 * 
 * Transaction Management:
 * - @Transactional(readOnly = true) on class for read operations
 * - @Transactional on methods that modify data
 * - Ensures data consistency and rollback on errors
 * 
 * Exception Handling:
 * - Services throw domain exceptions (ResourceNotFoundException, etc.)
 * - GlobalExceptionHandler converts to HTTP responses
 * - Controllers don't catch exceptions (clean code)
 * 
 * Usage Example:
 * <pre>
 * {@literal @}Service
 * {@literal @}RequiredArgsConstructor
 * {@literal @}Transactional(readOnly = true)
 * public class CourseService {
 *     
 *     private final CourseRepository courseRepository;
 *     private final UserRepository userRepository;
 *     
 *     // GRASP: Creator - creates Course from DTO
 *     {@literal @}Transactional
 *     public CourseResponseDto createCourse(CourseCreateDto dto, UUID instructorId) {
 *         User instructor = userRepository.findById(instructorId)
 *             .orElseThrow(() -> new ResourceNotFoundException("User", instructorId));
 *         
 *         // GRASP: Information Expert - validates business rules
 *         if (!instructor.isInstructor()) {
 *             throw new IllegalArgumentException("Only instructors can create courses");
 *         }
 *         
 *         Course course = new Course();
 *         // ... set properties from DTO
 *         
 *         Course saved = courseRepository.save(course);
 *         return CourseResponseDto.fromEntity(saved);
 *     }
 *     
 *     // GRASP: Information Expert - knows publishing rules
 *     {@literal @}Transactional
 *     public void publishCourse(UUID courseId) {
 *         Course course = getCourse(courseId);
 *         
 *         if (course.getStatus() != CourseStatus.DRAFT) {
 *             throw new BusinessRuleViolationException("Only draft courses can be published");
 *         }
 *         
 *         course.publish();
 *         courseRepository.save(course);
 *     }
 * }
 * </pre>
 * 
 * Best Practices Followed:
 * - Constructor injection with @RequiredArgsConstructor (Lombok)
 * - Never expose entities - always return DTOs
 * - Clear, descriptive method names
 * - Comprehensive logging
 * - Proper exception handling
 * - Transaction boundaries at service methods
 * - Business logic in services, not controllers or repositories
 * 
 * @since 1.0.0
 */
package com.activwork.etms.service;
