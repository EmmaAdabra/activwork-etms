package com.activwork.etms.model;

/**
 * Session status enum matching PostgreSQL session_status type.
 * Tracks the lifecycle of live training sessions.
 */
public enum SessionStatus {
    /**
     * Scheduled - session is planned for the future
     */
    SCHEDULED,
    
    /**
     * Live - session is currently in progress
     */
    LIVE,
    
    /**
     * Completed - session has ended
     */
    COMPLETED,
    
    /**
     * Cancelled - session was cancelled
     */
    CANCELLED
}

