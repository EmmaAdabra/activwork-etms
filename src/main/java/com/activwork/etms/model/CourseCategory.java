package com.activwork.etms.model;

/**
 * Course category enum matching PostgreSQL course_category type.
 * Categorizes courses into different IT domains.
 */
public enum CourseCategory {
    /**
     * Programming languages and fundamentals
     */
    PROGRAMMING,
    
    /**
     * Web development technologies (frontend and backend)
     */
    WEB_DEVELOPMENT,
    
    /**
     * Database design, SQL, and NoSQL technologies
     */
    DATABASE,
    
    /**
     * DevOps practices, CI/CD, containerization
     */
    DEVOPS,
    
    /**
     * Cybersecurity, penetration testing, security protocols
     */
    CYBERSECURITY,
    
    /**
     * Data science, machine learning, analytics
     */
    DATA_SCIENCE,
    
    /**
     * Mobile app development (iOS, Android, cross-platform)
     */
    MOBILE_DEVELOPMENT,
    
    /**
     * Cloud computing platforms and services
     */
    CLOUD_COMPUTING,
    
    /**
     * Software testing, QA, test automation
     */
    SOFTWARE_TESTING,
    
    /**
     * Project management methodologies and tools
     */
    PROJECT_MANAGEMENT
}

