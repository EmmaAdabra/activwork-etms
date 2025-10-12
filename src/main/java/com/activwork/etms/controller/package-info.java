/**
 * Controller layer package for MVC pattern implementation.
 * 
 * This package implements the Presentation Layer (Controller) of the layered architecture
 * and completes the MVC pattern with Model (entities/DTOs) and View (Thymeleaf templates).
 * 
 * Controllers (MVC):
 * - AuthController: Authentication operations (login, register, logout)
 * - HomeController: Public pages (homepage, browse courses)
 * - DashboardController: Role-based dashboard routing
 * - InstructorController: Instructor operations (course management)
 * - LearnerController: Learner operations (enroll, feedback, progress)
 * 
 * MVC Pattern Implementation:
 * <pre>
 * Model (M):
 *   - Domain entities (User, Course, Enrollment, etc.)
 *   - DTOs (CourseResponseDto, EnrollmentResponseDto, etc.)
 *   - Service layer (business logic)
 *   
 * View (V):
 *   - Thymeleaf templates (*.html in templates/)
 *   - Dynamic HTML generation
 *   - User interface
 *   
 * Controller (C):  ← THIS PACKAGE
 *   - HTTP request handling
 *   - Input validation
 *   - Service delegation
 *   - View selection
 *   - Response generation
 * </pre>
 * 
 * GRASP Pattern: Controller
 * - Controllers are the first objects beyond the UI layer
 * - Receive system operations (HTTP requests)
 * - Delegate to domain objects (services)
 * - Don't contain business logic themselves
 * - Coordinate and route requests
 * 
 * Controller Responsibilities:
 * 1. Receive HTTP requests
 * 2. Validate input (Bean Validation with @Valid)
 * 3. Extract authentication principal
 * 4. Call appropriate service methods
 * 5. Handle service exceptions (via GlobalExceptionHandler)
 * 6. Select view template
 * 7. Populate model for view
 * 8. Return view name or redirect
 * 
 * Security Integration:
 * - @AuthenticationPrincipal for getting logged-in user
 * - Role-based access control via Spring Security
 * - Session management (configured in SecurityConfig)
 * 
 * Request Flow Example:
 * <pre>
 * User clicks "Enroll" button
 *     ↓ POST /courses/{id}/enroll
 * LearnerController.enrollInCourse()
 *     ↓ Gets authenticated user
 *     ↓ Creates EnrollmentRequestDto
 *     ↓ Calls enrollmentService.enrollLearner()
 * EnrollmentService validates business rules
 *     ↓ Checks: already enrolled? course full? deadline passed?
 *     ↓ Creates Enrollment entity
 *     ↓ Saves via repository
 *     ↓ Returns EnrollmentResponseDto
 * Controller receives result
 *     ↓ Adds success message
 *     ↓ Returns "redirect:/learner/dashboard"
 * Spring MVC redirects browser
 *     ↓ User sees updated dashboard
 * </pre>
 * 
 * Design Patterns:
 * - Front Controller (Spring DispatcherServlet)
 * - MVC (Model-View-Controller)
 * - GRASP Controller (request routing)
 * - Post-Redirect-Get (PRG) pattern for form submissions
 * 
 * Best Practices Followed:
 * - Constructor injection (@RequiredArgsConstructor)
 * - Logging (SLF4J)
 * - Validation (@Valid with BindingResult)
 * - Flash attributes for messages
 * - Redirect after POST (PRG pattern)
 * - Never expose entities - always use DTOs
 * - Exception handling delegated to @ControllerAdvice
 * 
 * URL Structure:
 * - Public: /, /login, /register, /courses
 * - Instructor: /instructor/**
 * - Learner: /learner/**
 * - Shared: /dashboard (routes based on role)
 * 
 * @since 1.0.0
 */
package com.activwork.etms.controller;
