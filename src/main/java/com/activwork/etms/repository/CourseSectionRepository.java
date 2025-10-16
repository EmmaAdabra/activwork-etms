package com.activwork.etms.repository;

import com.activwork.etms.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for CourseSection entity.
 * Provides data access methods for course sections.
 * 
 * GRASP Pattern: Pure Fabrication
 * - Created solely for database persistence concerns
 */
@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {

    /**
     * Find all sections for a specific course, ordered by section order
     * @param courseId the course ID
     * @return list of course sections
     */
    List<CourseSection> findByCourseIdOrderBySectionOrderAsc(UUID courseId);

    /**
     * Find all active sections for a specific course
     * @param courseId the course ID
     * @param isActive the active status
     * @return list of active course sections
     */
    List<CourseSection> findByCourseIdAndIsActiveOrderBySectionOrderAsc(UUID courseId, Boolean isActive);

    /**
     * Count total sections in a course
     * @param courseId the course ID
     * @return count of sections
     */
    long countByCourseId(UUID courseId);

    /**
     * Count active sections in a course
     * @param courseId the course ID
     * @param isActive the active status
     * @return count of active sections
     */
    long countByCourseIdAndIsActive(UUID courseId, Boolean isActive);

    /**
     * Find section by course and order
     * @param courseId the course ID
     * @param sectionOrder the section order
     * @return optional course section
     */
    java.util.Optional<CourseSection> findByCourseIdAndSectionOrder(UUID courseId, Integer sectionOrder);

    /**
     * Get sections with their materials eagerly loaded
     * @param courseId the course ID
     * @return list of sections with materials
     */
    @Query("SELECT DISTINCT s FROM CourseSection s " +
           "LEFT JOIN FETCH s.materials m " +
           "WHERE s.course.id = :courseId " +
           "AND s.isActive = true " +
           "ORDER BY s.sectionOrder ASC")
    List<CourseSection> findSectionsWithMaterialsByCourseId(@Param("courseId") UUID courseId);

    /**
     * Get sections with their active materials only
     * @param courseId the course ID
     * @return list of sections with active materials
     */
    @Query("SELECT DISTINCT s FROM CourseSection s " +
           "LEFT JOIN FETCH s.materials m " +
           "WHERE s.course.id = :courseId " +
           "AND s.isActive = true " +
           "AND (m.isActive = true OR m IS NULL) " +
           "ORDER BY s.sectionOrder ASC, m.materialOrder ASC")
    List<CourseSection> findSectionsWithActiveMaterialsByCourseId(@Param("courseId") UUID courseId);

    /**
     * Delete all sections for a course
     * @param courseId the course ID
     */
    void deleteByCourseId(UUID courseId);

    /**
     * Check if a section exists for a course
     * @param courseId the course ID
     * @param sectionId the section ID
     * @return true if section exists
     */
    boolean existsByIdAndCourseId(UUID sectionId, UUID courseId);
}

