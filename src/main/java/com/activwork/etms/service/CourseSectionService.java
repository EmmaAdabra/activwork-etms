package com.activwork.etms.service;

import com.activwork.etms.dto.CourseSectionCreateDto;
import com.activwork.etms.dto.CourseSectionDto;
import com.activwork.etms.dto.MaterialResponseDto;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.Course;
import com.activwork.etms.model.CourseSection;
import com.activwork.etms.model.Material;
import com.activwork.etms.repository.CourseSectionRepository;
import com.activwork.etms.repository.CourseRepository;
import com.activwork.etms.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing course sections.
 * Handles section creation, updates, and retrieval.
 * 
 * GRASP Pattern: Controller
 * - Coordinates section-related business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseSectionService {

    private final CourseSectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final MaterialRepository materialRepository;

    /**
     * Get all sections for a course
     * @param courseId the course ID
     * @return list of section DTOs
     */
    public List<CourseSectionDto> getSectionsByCourseId(UUID courseId) {
        log.info("Fetching sections for course: {}", courseId);
        
        List<CourseSection> sections = sectionRepository
                .findByCourseIdAndIsActiveOrderBySectionOrderAsc(courseId, true);
        
        return sections.stream()
                .map(CourseSectionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all sections with their materials for a course
     * @param courseId the course ID
     * @return list of section DTOs with materials
     */
    public List<CourseSectionDto> getSectionsWithMaterialsByCourseId(UUID courseId) {
        log.info("Fetching sections with materials for course: {}", courseId);
        
        List<CourseSection> sections = sectionRepository
                .findSectionsWithActiveMaterialsByCourseId(courseId);
        
        return sections.stream()
                .map(CourseSectionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get a single section by ID
     * @param sectionId the section ID
     * @return section DTO
     */
    public CourseSectionDto getSectionById(UUID sectionId) {
        log.info("Fetching section: {}", sectionId);
        
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));
        
        return CourseSectionDto.fromEntity(section);
    }

    /**
     * Create a new section for a course
     * @param courseId the course ID
     * @param createDto the section creation DTO
     * @return created section DTO
     */
    @Transactional
    public CourseSectionDto createSection(UUID courseId, CourseSectionCreateDto createDto) {
        log.info("Creating section for course: {}", courseId);
        
        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Create new section
        CourseSection section = new CourseSection();
        section.setCourse(course);
        section.setTitle(createDto.getTitle());
        section.setDescription(createDto.getDescription());
        section.setSectionOrder(createDto.getSectionOrder());
        section.setIsActive(createDto.getIsActive());
        
        // Save section
        CourseSection savedSection = sectionRepository.save(section);
        
        log.info("Section created: {} for course: {}", savedSection.getId(), courseId);
        
        return CourseSectionDto.fromEntity(savedSection);
    }

    /**
     * Update an existing section
     * @param sectionId the section ID
     * @param updateDto the section update DTO
     * @return updated section DTO
     */
    @Transactional
    public CourseSectionDto updateSection(UUID sectionId, CourseSectionCreateDto updateDto) {
        log.info("Updating section: {}", sectionId);
        
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));
        
        // Update fields
        section.setTitle(updateDto.getTitle());
        section.setDescription(updateDto.getDescription());
        section.setSectionOrder(updateDto.getSectionOrder());
        section.setIsActive(updateDto.getIsActive());
        
        // Save updates
        CourseSection updatedSection = sectionRepository.save(section);
        
        log.info("Section updated: {}", sectionId);
        
        return CourseSectionDto.fromEntity(updatedSection);
    }

    /**
     * Delete a section (soft delete by setting isActive to false)
     * @param sectionId the section ID
     */
    @Transactional
    public void deleteSection(UUID sectionId) {
        log.info("Deleting section: {}", sectionId);
        
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));
        
        // Soft delete
        section.setIsActive(false);
        sectionRepository.save(section);
        
        log.info("Section deleted (soft): {}", sectionId);
    }

    /**
     * Permanently delete a section and its materials
     * @param sectionId the section ID
     */
    @Transactional
    public void permanentlyDeleteSection(UUID sectionId) {
        log.info("Permanently deleting section: {}", sectionId);
        
        // Materials will be deleted automatically due to cascade
        sectionRepository.deleteById(sectionId);
        
        log.info("Section permanently deleted: {}", sectionId);
    }

    /**
     * Add a material to a section
     * @param sectionId the section ID
     * @param materialId the material ID
     */
    @Transactional
    public void addMaterialToSection(UUID sectionId, UUID materialId) {
        log.info("Adding material {} to section {}", materialId, sectionId);
        
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        
        // Verify material belongs to the same course
        if (!material.getCourse().getId().equals(section.getCourse().getId())) {
            throw new IllegalArgumentException("Material does not belong to the same course as the section");
        }
        
        // Add material to section
        material.setSection(section);
        materialRepository.save(material);
        
        log.info("Material {} added to section {}", materialId, sectionId);
    }

    /**
     * Remove a material from a section
     * @param materialId the material ID
     */
    @Transactional
    public void removeMaterialFromSection(UUID materialId) {
        log.info("Removing material {} from its section", materialId);
        
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        
        material.setSection(null);
        materialRepository.save(material);
        
        log.info("Material {} removed from section", materialId);
    }

    /**
     * Reorder sections within a course
     * @param courseId the course ID
     * @param sectionOrders map of section IDs to new order values
     */
    @Transactional
    public void reorderSections(UUID courseId, List<UUID> orderedSectionIds) {
        log.info("Reordering sections for course: {}", courseId);
        
        for (int i = 0; i < orderedSectionIds.size(); i++) {
            UUID sectionId = orderedSectionIds.get(i);
            CourseSection section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("CourseSection", sectionId));
            
            // Verify section belongs to the course
            if (!section.getCourse().getId().equals(courseId)) {
                throw new IllegalArgumentException("Section does not belong to the specified course");
            }
            
            section.setSectionOrder(i);
            sectionRepository.save(section);
        }
        
        log.info("Sections reordered for course: {}", courseId);
    }

    /**
     * Count sections in a course
     * @param courseId the course ID
     * @return count of active sections
     */
    public long countSectionsByCourse(UUID courseId) {
        return sectionRepository.countByCourseIdAndIsActive(courseId, true);
    }
}

