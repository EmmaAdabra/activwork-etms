package com.activwork.etms.model;

/**
 * Material type enum matching PostgreSQL material_type type.
 * Categorizes course materials for better organization and display.
 */
public enum MaterialType {
    /**
     * Video content (MP4, AVI, MOV)
     */
    VIDEO,
    
    /**
     * PDF documents
     */
    PDF,
    
    /**
     * Document files (DOCX, TXT)
     */
    DOCUMENT,
    
    /**
     * Presentation files (PPTX, PPT)
     */
    PRESENTATION,
    
    /**
     * Audio files (MP3, WAV)
     */
    AUDIO,
    
    /**
     * Image files (PNG, JPG, JPEG)
     */
    IMAGE,
    
    /**
     * Code samples and examples
     */
    CODE_SAMPLE,
    
    /**
     * Practice exercises
     */
    EXERCISE,
    
    /**
     * Quiz or assessment materials
     */
    QUIZ,
    
    /**
     * Certificate templates or files
     */
    CERTIFICATE
}

