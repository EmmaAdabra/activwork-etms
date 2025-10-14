package com.activwork.etms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC Configuration.
 * Configures resource handlers for serving uploaded files.
 * 
 * Security Notes:
 * - Files are served via controlled endpoints
 * - Access control should be implemented at controller level
 * - No directory traversal allowed (handled by FileStorageService)
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    /**
     * Configure resource handlers to serve uploaded files.
     * 
     * Endpoints:
     * - /uploads/banners/** → serves course banner images
     * - /uploads/materials/** → serves course materials
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString() + "/";
        
        // Serve banner images
        registry.addResourceHandler("/uploads/banners/**")
                .addResourceLocations(uploadLocation + "banners/")
                .setCachePeriod(3600); // Cache for 1 hour
        
        // Serve course materials
        registry.addResourceHandler("/uploads/materials/**")
                .addResourceLocations(uploadLocation + "materials/")
                .setCachePeriod(3600);
    }
}

