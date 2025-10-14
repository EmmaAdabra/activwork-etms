package com.activwork.etms.service;

import com.activwork.etms.config.FileStorageProperties;
import com.activwork.etms.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for handling file storage operations.
 * 
 * GRASP Pattern: Information Expert
 * - Expert on file system operations
 * - Knows how to store and retrieve files
 * - Validates file types and sizes
 * 
 * Business Rules:
 * 1. Files stored with unique UUID-based names to prevent conflicts
 * 2. Original filenames preserved for user-facing display
 * 3. Files organized by type (banners, materials)
 * 4. Maximum file size enforced (configurable, default 50MB)
 */
@Slf4j
@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final Path bannerStorageLocation;
    private final Path materialStorageLocation;
    private final long maxFileSize;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.maxFileSize = fileStorageProperties.getMaxSize();
        
        // Create base upload directory
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        
        // Create subdirectories for different file types
        this.bannerStorageLocation = this.fileStorageLocation.resolve("banners");
        this.materialStorageLocation = this.fileStorageLocation.resolve("materials");
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.bannerStorageLocation);
            Files.createDirectories(this.materialStorageLocation);
            
            log.info("File storage initialized at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }

    /**
     * Store a course banner image.
     * 
     * @param file the uploaded file
     * @return the stored filename (UUID-based)
     * @throws FileStorageException if storage fails
     */
    public String storeBanner(MultipartFile file) {
        return storeFile(file, bannerStorageLocation, "banner");
    }

    /**
     * Store course material file.
     * 
     * @param file the uploaded file
     * @return the stored filename (UUID-based)
     * @throws FileStorageException if storage fails
     */
    public String storeMaterial(MultipartFile file) {
        return storeFile(file, materialStorageLocation, "material");
    }

    /**
     * Store a file in the specified location.
     * 
     * @param file the uploaded file
     * @param targetLocation the directory to store the file
     * @param fileType the type of file (for logging)
     * @return the stored filename
     * @throws FileStorageException if storage fails
     */
    private String storeFile(MultipartFile file, Path targetLocation, String fileType) {
        // Validate file
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new FileStorageException(
                String.format("File size exceeds maximum allowed size of %d bytes", maxFileSize)
            );
        }

        // Get original filename (throws NPE if null, which is caught and wrapped below)
        String originalFilenameRaw = Objects.requireNonNull(
            file.getOriginalFilename(), 
            "File must have a valid filename"
        );
        
        String originalFilename = StringUtils.cleanPath(originalFilenameRaw);
        
        // Validate filename
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Invalid filename: " + originalFilename);
        }

        // Generate unique filename with original extension
        String extension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + extension;

        try {
            // Copy file to target location
            Path targetPath = targetLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("Stored {} file: {} (original: {})", fileType, storedFilename, originalFilename);
            
            return storedFilename;
            
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file: " + originalFilename, ex);
        }
    }

    /**
     * Load a file as a Resource.
     * 
     * @param filename the filename
     * @param fileType the type (banner or material)
     * @return the file resource
     * @throws FileStorageException if file not found
     */
    public Resource loadFileAsResource(String filename, String fileType) {
        try {
            Path filePath;
            if ("banner".equalsIgnoreCase(fileType)) {
                filePath = bannerStorageLocation.resolve(filename).normalize();
            } else {
                filePath = materialStorageLocation.resolve(filename).normalize();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + filename, ex);
        }
    }

    /**
     * Delete a file.
     * 
     * @param filename the filename to delete
     * @param fileType the type (banner or material)
     * @throws FileStorageException if deletion fails
     */
    public void deleteFile(String filename, String fileType) {
        try {
            Path filePath;
            if ("banner".equalsIgnoreCase(fileType)) {
                filePath = bannerStorageLocation.resolve(filename).normalize();
            } else {
                filePath = materialStorageLocation.resolve(filename).normalize();
            }
            
            Files.deleteIfExists(filePath);
            log.info("Deleted {} file: {}", fileType, filename);
            
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", filename, ex);
            throw new FileStorageException("Failed to delete file: " + filename, ex);
        }
    }

    /**
     * Get file extension from filename.
     * 
     * @param filename the filename
     * @return the extension (including dot) or empty string
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        
        return "";
    }

    /**
     * Validate if file is an image.
     * 
     * @param file the file to validate
     * @return true if file is an image
     */
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Get MIME type from file extension.
     * 
     * @param filename the filename
     * @return the MIME type
     */
    public String getMimeType(String filename) {
        try {
            Path path = Paths.get(filename);
            return Files.probeContentType(path);
        } catch (IOException ex) {
            return "application/octet-stream";
        }
    }
}

