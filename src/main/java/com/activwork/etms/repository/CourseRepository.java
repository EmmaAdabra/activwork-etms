package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.CourseCategory;
import com.activwork.etms.model.CourseStatus;
import com.activwork.etms.model.DifficultyLevel;
import com.activwork.etms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Course entity operations.
 * 
 * Provides data access methods for training courses.
 * Includes custom queries for searching and filtering courses.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Find all courses by instructor.
     * 
     * @param instructor the instructor user
     * @return list of courses taught by the instructor
     */
    List<Course> findByInstructor(User instructor);

    /**
     * Find all courses by instructor ID.
     * 
     * @param instructorId the instructor's UUID
     * @return list of courses taught by the instructor
     */
    List<Course> findByInstructorId(UUID instructorId);

    /**
     * Find all courses by category.
     * 
     * @param category the course category
     * @return list of courses in the category
     */
    List<Course> findByCategory(CourseCategory category);

    /**
     * Find all courses by status.
     * 
     * @param status the course status (DRAFT, PUBLISHED, ARCHIVED, SUSPENDED)
     * @return list of courses with the specified status
     */
    List<Course> findByStatus(CourseStatus status);

    /**
     * Find all courses by difficulty level.
     * 
     * @param difficultyLevel the difficulty level
     * @return list of courses with the specified difficulty
     */
    List<Course> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    /**
     * Find all active courses.
     * 
     * @param isActive true for active courses, false for inactive
     * @return list of courses matching the active status
     */
    List<Course> findByIsActive(Boolean isActive);

    /**
     * Find all featured courses.
     * 
     * @param isFeatured true for featured courses
     * @return list of featured courses
     */
    List<Course> findByIsFeatured(Boolean isFeatured);

    /**
     * Find all published and active courses (available for enrollment).
     * 
     * @return list of courses that are published and active
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' AND c.isActive = true")
    List<Course> findAvailableCourses();

    /**
     * Search courses by title containing keyword (case-insensitive).
     * 
     * @param keyword the search keyword
     * @return list of courses with matching titles
     */
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByTitle(@Param("keyword") String keyword);

    /**
     * Find courses by category and difficulty level.
     * 
     * @param category the course category
     * @param difficultyLevel the difficulty level
     * @return list of matching courses
     */
    List<Course> findByCategoryAndDifficultyLevel(CourseCategory category, DifficultyLevel difficultyLevel);

    /**
     * Find published courses by instructor.
     * 
     * @param instructorId the instructor's UUID
     * @param status the course status
     * @return list of instructor's courses with specified status
     */
    List<Course> findByInstructorIdAndStatus(UUID instructorId, CourseStatus status);
}

