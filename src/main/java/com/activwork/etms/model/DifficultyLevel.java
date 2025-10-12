package com.activwork.etms.model;

/**
 * Difficulty level enum matching PostgreSQL difficulty_level type.
 * Indicates the complexity and prerequisite knowledge required for a course.
 */
public enum DifficultyLevel {
    /**
     * Beginner level - no prior knowledge required
     */
    BEGINNER,
    
    /**
     * Intermediate level - basic knowledge required
     */
    INTERMEDIATE,
    
    /**
     * Advanced level - solid foundation required
     */
    ADVANCED,
    
    /**
     * Expert level - extensive experience required
     */
    EXPERT
}

