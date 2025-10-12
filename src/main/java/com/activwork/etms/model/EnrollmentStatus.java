package com.activwork.etms.model;

/**
 * Enrollment status enum matching PostgreSQL enrollment_status type.
 * Tracks the lifecycle of a learner's enrollment in a course.
 */
public enum EnrollmentStatus {
    /**
     * Active enrollment - learner is currently taking the course
     */
    ACTIVE,
    
    /**
     * Completed - learner has finished the course (100% progress)
     */
    COMPLETED,
    
    /**
     * Cancelled - learner withdrew from the course
     */
    CANCELLED,
    
    /**
     * Suspended - enrollment temporarily on hold
     */
    SUSPENDED
}

