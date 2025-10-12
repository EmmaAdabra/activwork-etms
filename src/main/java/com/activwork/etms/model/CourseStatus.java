package com.activwork.etms.model;

/**
 * Course status enum matching PostgreSQL course_status type.
 * Manages the lifecycle of a course from creation to archival.
 */
public enum CourseStatus {
    /**
     * Draft - course is being created/edited, not visible to learners
     */
    DRAFT,
    
    /**
     * Published - course is live and available for enrollment
     */
    PUBLISHED,
    
    /**
     * Archived - course is no longer active but preserved for records
     */
    ARCHIVED,
    
    /**
     * Suspended - course temporarily unavailable
     */
    SUSPENDED
}

