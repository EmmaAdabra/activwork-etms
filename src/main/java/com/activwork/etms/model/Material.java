package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Material entity representing course materials (files, videos, documents).
 * Maps to the 'materials' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for material metadata and file information
 */
@Entity
@Table(name = "materials", schema = "etms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank(message = "Filename is required")
    @Size(min = 1, max = 255, message = "Filename must be between 1 and 255 characters")
    @Column(nullable = false, length = 255)
    private String filename;

    @NotBlank(message = "Original filename is required")
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @NotBlank(message = "MIME type is required")
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @NotNull(message = "Material type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 50)
    private MaterialType materialType;

    @NotBlank(message = "Path is required")
    @Column(nullable = false, length = 500)
    private String path;

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be at least 1 byte")
    @Max(value = 52428800, message = "File size must not exceed 50MB")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Min(value = 1, message = "Duration must be positive")
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Min(value = 0, message = "Download count must be non-negative")
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Min(value = 0, message = "View count must be non-negative")
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_downloadable")
    private Boolean isDownloadable = true;

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Min(value = 0, message = "Display order must be non-negative")
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private ZonedDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Set upload timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = ZonedDateTime.now();
        }
    }

    /**
     * Check if material is a video
     * @return true if material type is VIDEO
     */
    public boolean isVideo() {
        return MaterialType.VIDEO.equals(this.materialType);
    }

    /**
     * Check if material is a document
     * @return true if material type is PDF, DOCUMENT, or PRESENTATION
     */
    public boolean isDocument() {
        return MaterialType.PDF.equals(this.materialType) ||
               MaterialType.DOCUMENT.equals(this.materialType) ||
               MaterialType.PRESENTATION.equals(this.materialType);
    }

    /**
     * Get file size in megabytes
     * @return file size in MB
     */
    public double getFileSizeMB() {
        return this.fileSize / (1024.0 * 1024.0);
    }

    /**
     * Increment download count
     */
    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
}

