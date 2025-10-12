/**
 * Security package for authentication and authorization.
 * 
 * This package contains Spring Security configuration and custom implementations
 * for securing the ETMS application.
 * 
 * Components:
 * - SecurityConfig: Main security configuration
 * - CustomUserDetailsService: User authentication implementation
 * 
 * Security Architecture:
 * 
 * 1. Authentication (Session-based):
 *    - User submits email + password
 *    - CustomUserDetailsService loads user from database
 *    - Spring Security validates password (BCrypt)
 *    - HTTP session created on successful login
 *    - Session ID stored in cookie (JSESSIONID)
 *    - 30-minute session timeout (configured in application.properties)
 * 
 * 2. Authorization (Role-based):
 *    - INSTRUCTOR role: Can create/manage courses, view enrollments
 *    - LEARNER role: Can enroll in courses, submit feedback
 *    - Authorities configured in CustomUserDetailsService
 *    - Access control rules in SecurityConfig
 * 
 * 3. Password Security:
 *    - BCrypt hashing (BCryptPasswordEncoder)
 *    - Salted hashes (automatic with BCrypt)
 *    - One-way encryption (cannot reverse)
 *    - Password strength: min 8 characters (validated in DTO)
 * 
 * 4. Session Security:
 *    - HTTP-only cookies (prevents XSS)
 *    - CSRF protection enabled (for forms)
 *    - One session per user
 *    - Auto-logout on inactivity
 * 
 * Access Control Matrix:
 * <pre>
 * Endpoint                  | Public | Learner | Instructor | Admin
 * --------------------------|--------|---------|------------|-------
 * /, /login, /register      |   ✓    |    ✓    |      ✓     |   ✓
 * /courses (browse)         |   ✓    |    ✓    |      ✓     |   ✓
 * /courses/{id} (view)      |   ✓    |    ✓    |      ✓     |   ✓
 * /learner/**               |   ✗    |    ✓    |      ✗     |   ✓
 * /instructor/**            |   ✗    |    ✗    |      ✓     |   ✓
 * /dashboard                |   ✗    |    ✓    |      ✓     |   ✓
 * </pre>
 * 
 * Future Enhancements:
 * - JWT authentication (stateless)
 * - OAuth2 integration (Google, GitHub)
 * - Two-factor authentication (2FA)
 * - Remember-me functionality
 * - Account lockout after failed attempts
 * 
 * Configuration:
 * Session timeout, cookie settings, and CORS are configured in:
 * - application.properties
 * - SecurityConfig.java
 * 
 * @since 1.0.0
 */
package com.activwork.etms.security;
