package com.activwork.etms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for file storage.
 * Reads file upload settings from application.properties.
 * 
 * GRASP Pattern: Information Expert
 * - Knows file storage configuration
 * - Provides upload directory and size limits
 */
@Configuration
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageProperties {
    
    /**
     * Directory where uploaded files will be stored
     */
    private String uploadDir = "uploads/";
    
    /**
     * Maximum file size in bytes (default 50MB)
     */
    private long maxSize = 52428800L; // 50MB
}

