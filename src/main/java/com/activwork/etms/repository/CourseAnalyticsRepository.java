package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.CourseAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for CourseAnalytics entity operations.
 * 
 * Provides data access methods for course performance metrics and analytics.
 * Enables instructor dashboards with historical data and trend analysis.
 */
@Repository
public interface CourseAnalyticsRepository extends JpaRepository<CourseAnalytics, UUID> {

    /**
     * Find all analytics records for a specific course.
     * 
     * @param course the course
     * @return list of analytics records for the course
     */
    List<CourseAnalytics> findByCourse(Course course);

    /**
     * Find all analytics records for a specific course ID.
     * 
     * @param courseId the course UUID
     * @return list of analytics records for the course
     */
    List<CourseAnalytics> findByCourseId(UUID courseId);

    /**
     * Find all analytics records for a course ordered by date (newest first).
     * 
     * @param courseId the course UUID
     * @return list of analytics sorted by date descending
     */
    List<CourseAnalytics> findByCourseIdOrderByDateDesc(UUID courseId);

    /**
     * Find analytics for a specific course on a specific date.
     * 
     * @param courseId the course UUID
     * @param date the date
     * @return Optional containing the analytics if found
     */
    Optional<CourseAnalytics> findByCourseIdAndDate(UUID courseId, LocalDate date);

    /**
     * Find analytics for a course within a date range.
     * 
     * @param courseId the course UUID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of analytics in the date range
     */
    @Query("SELECT ca FROM CourseAnalytics ca WHERE ca.course.id = :courseId AND ca.date BETWEEN :startDate AND :endDate ORDER BY ca.date ASC")
    List<CourseAnalytics> findByCourseIdAndDateRange(
            @Param("courseId") UUID courseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find the most recent analytics record for a course.
     * 
     * @param courseId the course UUID
     * @return Optional containing the most recent analytics
     */
    @Query("SELECT ca FROM CourseAnalytics ca WHERE ca.course.id = :courseId ORDER BY ca.date DESC LIMIT 1")
    Optional<CourseAnalytics> findLatestForCourse(@Param("courseId") UUID courseId);

    /**
     * Check if analytics exist for a course on a specific date.
     * 
     * @param courseId the course UUID
     * @param date the date
     * @return true if analytics exist for that date
     */
    boolean existsByCourseIdAndDate(UUID courseId, LocalDate date);

    /**
     * Calculate total revenue for a course across all time.
     * 
     * @param courseId the course UUID
     * @return total revenue
     */
    @Query("SELECT COALESCE(SUM(ca.revenue), 0.0) FROM CourseAnalytics ca WHERE ca.course.id = :courseId")
    Double calculateTotalRevenue(@Param("courseId") UUID courseId);

    /**
     * Calculate total views for a course across all time.
     * 
     * @param courseId the course UUID
     * @return total views
     */
    @Query("SELECT COALESCE(SUM(ca.views), 0) FROM CourseAnalytics ca WHERE ca.course.id = :courseId")
    Integer calculateTotalViews(@Param("courseId") UUID courseId);

    /**
     * Calculate total enrollments for a course across all time.
     * 
     * @param courseId the course UUID
     * @return total enrollments
     */
    @Query("SELECT COALESCE(SUM(ca.enrollments), 0) FROM CourseAnalytics ca WHERE ca.course.id = :courseId")
    Integer calculateTotalEnrollments(@Param("courseId") UUID courseId);

    /**
     * Find top performing courses by revenue in a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param limit the maximum number of results
     * @return list of top performing courses
     */
    @Query("SELECT ca.course, SUM(ca.revenue) as totalRevenue FROM CourseAnalytics ca WHERE ca.date BETWEEN :startDate AND :endDate GROUP BY ca.course ORDER BY totalRevenue DESC LIMIT :limit")
    List<Object[]> findTopCoursesByRevenue(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit
    );
}

