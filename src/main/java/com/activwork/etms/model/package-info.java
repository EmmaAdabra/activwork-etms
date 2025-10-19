/**
 * Domain model package containing JPA entities and enums.
 * 
 * This package implements the Entity layer of the Layered Architecture,
 * mapping database tables to Java objects using JPA/Hibernate.
 * 
 * Core Entities (6):
 * - User: System users (instructors and learners)
 * - Course: Training courses with lifecycle management
 * - Material: Course materials (videos, PDFs, documents)
 * - Enrollment: Learner enrollments with progress tracking
 * - Feedback: Course ratings and reviews
 * - LiveSession: Scheduled live training sessions
 * 
 * Innovative Entities (4):
 * - CoursePrerequisite: Course dependency management for learning paths
 * - MaterialProgress: Granular material completion tracking (video bookmarking)
 * - Notification: Real-time user notifications and alerts
 * - CourseAnalytics: Daily course performance metrics for instructors
 * 
 * Enums (7):
 * - UserRole: INSTRUCTOR, LEARNER
 * - CourseCategory: 10 categories for course classification
 * - EnrollmentStatus: ACTIVE, COMPLETED, CANCELLED, SUSPENDED
 * - MaterialType: 10 types (VIDEO, PDF, DOCUMENT, etc.)
 * - CourseStatus: DRAFT, PUBLISHED, ARCHIVED, SUSPENDED
 * - SessionStatus: SCHEDULED, LIVE, COMPLETED, CANCELLED
 * 
 * GRASP Patterns Applied:
 * - Information Expert: Each entity is responsible for its own data and business logic
 * - Creator: Entities create and manage related objects (Course creates Materials)
 * 
 * All entities:
 * - Use UUID primary keys for better scalability
 * - Include validation annotations for data integrity
 * - Map to the 'etms' PostgreSQL schema
 * - Use Lombok annotations to reduce boilerplate
 * - Include audit fields (created_at, updated_at)
 * - Implement lifecycle callbacks (@PrePersist, @PreUpdate)
 */
package com.activwork.etms.model;
