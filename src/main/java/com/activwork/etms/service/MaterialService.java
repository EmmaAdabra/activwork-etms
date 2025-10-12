package com.activwork.etms.service;

import com.activwork.etms.dto.MaterialResponseDto;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.Material;
import com.activwork.etms.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

