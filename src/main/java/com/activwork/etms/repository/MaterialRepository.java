package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.Material;
import com.activwork.etms.model.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Material entity operations.
 * 
 * Provides data access methods for course materials (files, videos, documents).
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    /**
     * Find all materials for a specific course.
     * 
     * @param course the course
     * @return list of materials for the course
     */
    List<Material> findByCourse(Course course);

    /**
     * Find all materials for a specific course ID.
     * 
     * @param courseId the course UUID
     * @return list of materials for the course
     */
    List<Material> findByCourseId(UUID courseId);

    /**
     * Find all materials by type.
     * 
     * @param materialType the type of material (VIDEO, PDF, DOCUMENT, etc.)
     * @return list of materials of the specified type
     */
    List<Material> findByMaterialType(MaterialType materialType);

    /**
     * Find all active materials for a course.
     * 
     * @param courseId the course UUID
     * @param isActive true for active materials
     * @return list of active materials for the course
     */
    List<Material> findByCourseIdAndIsActive(UUID courseId, Boolean isActive);

    /**
     * Find all required materials for a course.
     * 
     * @param courseId the course UUID
     * @param isRequired true for required materials
     * @return list of required materials
     */
    List<Material> findByCourseIdAndIsRequired(UUID courseId, Boolean isRequired);

    /**
     * Find materials for a course ordered by display order.
     * 
     * @param courseId the course UUID
     * @return list of materials sorted by display order
     */
    List<Material> findByCourseIdOrderByDisplayOrderAsc(UUID courseId);

    /**
     * Count total materials for a course.
     * 
     * @param courseId the course UUID
     * @return number of materials in the course
     */
    long countByCourseId(UUID courseId);

    /**
     * Count active materials for a course.
     * 
     * @param courseId the course UUID
     * @param isActive true for active materials
     * @return number of active materials
     */
    long countByCourseIdAndIsActive(UUID courseId, Boolean isActive);
}

