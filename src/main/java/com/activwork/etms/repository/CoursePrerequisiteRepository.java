package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for CoursePrerequisite entity operations.
 * 
 * Provides data access methods for managing course prerequisites and learning paths.
 * Enables structured curriculum design with course dependencies.
 */
@Repository
public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, UUID> {

    /**
     * Find all prerequisites for a specific course.
     * 
     * @param course the course
     * @return list of prerequisites for the course
     */
    List<CoursePrerequisite> findByCourse(Course course);

    /**
     * Find all prerequisites for a specific course ID.
     * 
     * @param courseId the course UUID
     * @return list of prerequisites for the course
     */
    List<CoursePrerequisite> findByCourseId(UUID courseId);

    /**
     * Find all mandatory prerequisites for a course.
     * 
     * @param courseId the course UUID
     * @param isMandatory true for mandatory prerequisites
     * @return list of mandatory prerequisites
     */
    List<CoursePrerequisite> findByCourseIdAndIsMandatory(UUID courseId, Boolean isMandatory);

    /**
     * Find all courses that have a specific course as a prerequisite.
     * (Reverse lookup: "Which courses require this course?")
     * 
     * @param prerequisiteCourseId the prerequisite course UUID
     * @return list of courses that require this prerequisite
     */
    List<CoursePrerequisite> findByPrerequisiteCourseId(UUID prerequisiteCourseId);

    /**
     * Check if a prerequisite relationship exists between two courses.
     * 
     * @param courseId the course UUID
     * @param prerequisiteCourseId the prerequisite course UUID
     * @return true if prerequisite relationship exists
     */
    boolean existsByCourseIdAndPrerequisiteCourseId(UUID courseId, UUID prerequisiteCourseId);

    /**
     * Get all courses in a learning path (courses that depend on each other).
     * 
     * @param courseId the starting course UUID
     * @return list of all prerequisites in the learning path
     */
    @Query("SELECT cp FROM CoursePrerequisite cp WHERE cp.course.id = :courseId OR cp.prerequisiteCourse.id = :courseId")
    List<CoursePrerequisite> findLearningPath(@Param("courseId") UUID courseId);
}

