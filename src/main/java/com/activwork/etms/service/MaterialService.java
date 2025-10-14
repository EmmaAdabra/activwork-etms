package com.activwork.etms.service;

import com.activwork.etms.dto.MaterialResponseDto;
import com.activwork.etms.exception.FileStorageException;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.Course;
import com.activwork.etms.model.Material;
import com.activwork.etms.model.MaterialType;
import com.activwork.etms.repository.CourseRepository;
import com.activwork.etms.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Material entity operations.
 * 
 * GRASP Pattern: Information Expert
 * - Expert on material management and file handling
 * - Knows rules about material types and organization
 * - Responsible for material analytics (views, downloads)
 * 
 * Business Rules Enforced:
 * 1. Only course instructor can upload materials
 * 2. File size limits (enforced at upload)
 * 3. Supported file types validation
 * 4. Display order management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    /**
     * Get all materials for a course (ordered by display order).
     * 
     * @param courseId the course UUID
     * @return list of materials
     */
    public List<MaterialResponseDto> getMaterialsByCourse(UUID courseId) {
        List<Material> materials = materialRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        return materials.stream()
                .map(MaterialResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all active materials for a course.
     * 
     * @param courseId the course UUID
     * @return list of active materials
     */
    public List<MaterialResponseDto> getActiveMaterialsByCourse(UUID courseId) {
        List<Material> materials = materialRepository.findByCourseIdAndIsActive(courseId, true);
        return materials.stream()
                .map(MaterialResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get material by ID.
     * 
     * @param materialId the material UUID
     * @return material response
     * @throws ResourceNotFoundException if material not found
     */
    public MaterialResponseDto getMaterialById(UUID materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        
        return MaterialResponseDto.fromEntity(material);
    }

    /**
     * Get required materials for a course.
     * 
     * @param courseId the course UUID
     * @return list of required materials
     */
    public List<MaterialResponseDto> getRequiredMaterials(UUID courseId) {
        List<Material> materials = materialRepository.findByCourseIdAndIsRequired(courseId, true);
        return materials.stream()
                .map(MaterialResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Increment material download count.
     * 
     * @param materialId the material UUID
     */
    @Transactional
    public void incrementDownloadCount(UUID materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        
        material.incrementDownloadCount();
        materialRepository.save(material);
    }

    /**
     * Increment material view count.
     * 
     * @param materialId the material UUID
     */
    @Transactional
    public void incrementViewCount(UUID materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        
        material.incrementViewCount();
        materialRepository.save(material);
    }

    /**
     * Delete material.
     * 
     * @param materialId the material UUID
     * @param instructorId the instructor deleting (must be course instructor)
     * @throws ResourceNotFoundException if material not found
     * @throws IllegalArgumentException if not authorized
     */
    @Transactional
    public void deleteMaterial(UUID materialId, UUID instructorId) {
        log.info("Deleting material: {} by instructor: {}", materialId, instructorId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        
        // Business rule: Only course instructor can delete materials
        if (!material.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can delete materials");
        }
        
        materialRepository.delete(material);
        
        log.info("Material deleted successfully: {}", materialId);
    }

    /**
     * Count total materials for a course.
     * 
     * @param courseId the course UUID
     * @return number of materials
     */
    public long countMaterialsByCourse(UUID courseId) {
        return materialRepository.countByCourseIdAndIsActive(courseId, true);
    }

    /**
     * Upload and save a material file for a course.
     * 
     * @param courseId the course UUID
     * @param file the uploaded file
     * @param materialType the type of material
     * @param description optional description
     * @param isRequired whether material is required
     * @param isDownloadable whether material can be downloaded
     * @param instructorId the instructor uploading (must be course instructor)
     * @return the created material
     * @throws ResourceNotFoundException if course not found
     * @throws IllegalArgumentException if not authorized or invalid file
     * @throws FileStorageException if file storage fails
     */
    @Transactional
    public MaterialResponseDto uploadMaterial(
            UUID courseId,
            MultipartFile file,
            MaterialType materialType,
            String description,
            Boolean isRequired,
            Boolean isDownloadable,
            UUID instructorId) {
        
        log.info("Uploading material for course: {} by instructor: {}", courseId, instructorId);
        
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        // Get course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Business rule: Only course instructor can upload materials
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can upload materials");
        }
        
        // Store file
        String storedFilename = fileStorageService.storeMaterial(file);
        
        // Get file metadata
        String originalFilename = file.getOriginalFilename();
        String mimeType = file.getContentType();
        long fileSize = file.getSize();
        
        // Get next display order
        long existingCount = materialRepository.countByCourseId(courseId);
        
        // Create material entity
        Material material = new Material();
        material.setCourse(course);
        material.setFilename(storedFilename);
        material.setOriginalFilename(originalFilename);
        material.setMimeType(mimeType != null ? mimeType : "application/octet-stream");
        material.setMaterialType(materialType);
        material.setPath("/uploads/materials/" + storedFilename);
        material.setFileSize(fileSize);
        material.setDescription(description);
        material.setIsRequired(isRequired != null ? isRequired : false);
        material.setIsDownloadable(isDownloadable != null ? isDownloadable : true);
        material.setDisplayOrder((int) existingCount);
        material.setIsActive(true);
        
        Material savedMaterial = materialRepository.save(material);
        
        log.info("Material uploaded successfully: {} for course: {}", savedMaterial.getId(), courseId);
        
        return MaterialResponseDto.fromEntity(savedMaterial);
    }

    /**
     * Determine material type from file.
     * 
     * @param file the uploaded file
     * @return the material type
     */
    public MaterialType determineMaterialType(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        if (contentType == null && filename != null) {
            contentType = fileStorageService.getMimeType(filename);
        }
        
        if (contentType == null) {
            return MaterialType.DOCUMENT;
        }
        
        // Video files
        if (contentType.startsWith("video/")) {
            return MaterialType.VIDEO;
        }
        
        // Audio files
        if (contentType.startsWith("audio/")) {
            return MaterialType.AUDIO;
        }
        
        // Image files
        if (contentType.startsWith("image/")) {
            return MaterialType.IMAGE;
        }
        
        // PDF files
        if (contentType.equals("application/pdf")) {
            return MaterialType.PDF;
        }
        
        // Presentation files
        if (filename != null && (contentType.contains("presentation") || 
            filename.toLowerCase().endsWith(".ppt") ||
            filename.toLowerCase().endsWith(".pptx"))) {
            return MaterialType.PRESENTATION;
        }
        
        // Default to document
        return MaterialType.DOCUMENT;
    }
}

