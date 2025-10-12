package com.activwork.etms.model;

/**
 * User role enum matching PostgreSQL user_role type.
 * Defines the two primary user types in the ETMS system.
 */
public enum UserRole {
    /**
     * Instructor role - can create and manage courses, upload materials,
     * schedule sessions, view enrollments and feedback.
     */
    INSTRUCTOR,
    
    /**
     * Learner role - can enroll in courses, access materials,
     * attend sessions, provide feedback, and track progress.
     */
    LEARNER
}

