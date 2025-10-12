/**
 * Exception handling package for the ETMS application.
 * 
 * This package implements a comprehensive exception handling strategy that provides:
 * - Clear, meaningful error messages
 * - Proper HTTP status codes
 * - Consistent error response format
 * - Centralized exception handling
 * 
 * Architecture Pattern:
 * Repository Layer: Returns Optional or List (no exception throwing)
 * Service Layer:    Throws domain exceptions with business context
 * Controller Layer: Doesn't catch - lets exceptions bubble up
 * GlobalExceptionHandler: Catches all, converts to HTTP responses
 * 
 * Exception Hierarchy:
 * - EtmsException (base)
 *   ├── ResourceNotFoundException (404) - Resource not found errors
 *   └── (More categories can be added as needed)
 * 
 * Exception Handling Flow:
 * 1. Service throws ResourceNotFoundException("User", userId)
 * 2. Exception bubbles up through controller
 * 3. GlobalExceptionHandler catches it
 * 4. Converts to 404 HTTP response with ErrorResponse JSON
 * 5. Client receives consistent error structure
 * 
 * Benefits:
 * - Controllers stay clean (no try-catch blocks)
 * - Services express business logic clearly
 * - Consistent error responses for frontend
 * - Easy to add new exception types
 * - Automatic logging of errors
 * 
 * Usage Example:
 * <pre>
 * // In Service Layer
 * public User getUserById(UUID id) {
 *     return userRepository.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException("User", id));
 * }
 * 
 * // GlobalExceptionHandler automatically converts to:
 * {
 *   "timestamp": "2025-10-12T13:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "User not found with ID: xxx",
 *   "path": "/api/users/xxx"
 * }
 * </pre>
 * 
 * Design Decisions:
 * - Minimal set initially (only ResourceNotFoundException)
 * - Add more exception types as business logic grows
 * - Don't over-engineer - build what we need
 * - Follow Spring Boot best practices
 * 
 * @since 1.0.0
 */
package com.activwork.etms.exception;
