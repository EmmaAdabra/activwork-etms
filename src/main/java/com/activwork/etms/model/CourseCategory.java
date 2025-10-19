package com.activwork.etms.model;

/**
 * Course category enum matching PostgreSQL course_category type.
 * Categorizes courses into technical, business, professional, and personal development domains.
 */
public enum CourseCategory {
    // Technical - Development & Engineering
    /**
     * Programming languages, algorithms, coding fundamentals
     */
    PROGRAMMING("Programming"),
    
    /**
     * Web development, desktop apps, full-stack development, APIs
     */
    SOFTWARE_DEVELOPMENT("Software Development"),
    
    /**
     * iOS, Android, cross-platform mobile apps
     */
    MOBILE_DEVELOPMENT("Mobile Development"),
    
    /**
     * Database design, SQL, NoSQL, data modeling
     */
    DATABASE_MANAGEMENT("Database Management"),
    
    /**
     * User interface design, user experience, prototyping, design tools
     */
    UI_UX_DESIGN("UI/UX Design"),
    
    /**
     * Software testing, QA, test automation, quality engineering
     */
    QUALITY_ASSURANCE("Quality Assurance"),
    
    // Technical - Infrastructure & Data
    /**
     * Cloud computing (AWS, Azure, GCP), CI/CD, containerization, infrastructure automation
     */
    CLOUD_DEVOPS("Cloud & DevOps"),
    
    /**
     * Security, penetration testing, ethical hacking, security protocols
     */
    CYBERSECURITY("Cybersecurity"),
    
    /**
     * Machine learning, artificial intelligence, data science, analytics, deep learning
     */
    DATA_SCIENCE_AI("Data Science & AI"),
    
    /**
     * Network infrastructure, system administration, IT support, helpdesk
     */
    IT_NETWORK_ADMIN("IT & Network Administration"),
    
    // Business & Management
    /**
     * Project management, product management, Agile, Scrum, Kanban
     */
    PROJECT_PRODUCT_MANAGEMENT("Project & Product Management"),
    
    /**
     * Business analysis, entrepreneurship, business strategy, startups
     */
    BUSINESS_ENTREPRENEURSHIP("Business & Entrepreneurship"),
    
    /**
     * Team leadership, people management, organizational development
     */
    LEADERSHIP_MANAGEMENT("Leadership & Management"),
    
    // Professional & Personal Development
    /**
     * Communication, presentation, technical writing, soft skills, career development
     */
    PROFESSIONAL_SKILLS("Professional Skills"),
    
    /**
     * Time management, productivity, emotional intelligence, mental health, stress management, work-life balance
     */
    PERSONAL_DEVELOPMENT_WELLNESS("Personal Development & Wellness"),
    
    // General
    /**
     * Miscellaneous courses and emerging topics
     */
    OTHERS("Others");

    private final String displayName;

    CourseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

