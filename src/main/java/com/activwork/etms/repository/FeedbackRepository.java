package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.Feedback;
import com.activwork.etms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Feedback entity operations.
 * 
 * Provides data access methods for course feedback and ratings.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    /**
     * Find all feedback for a specific course.
     * 
     * @param course the course
     * @return list of feedback for the course
     */
    List<Feedback> findByCourse(Course course);

    /**
     * Find all feedback for a specific course ID.
     * 
     * @param courseId the course UUID
     * @return list of feedback for the course
     */
    List<Feedback> findByCourseId(UUID courseId);

    /**
     * Find all visible feedback for a course.
     * 
     * @param courseId the course UUID
     * @param isVisible true for visible feedback
     * @return list of visible feedback
     */
    List<Feedback> findByCourseIdAndIsVisible(UUID courseId, Boolean isVisible);

    /**
     * Find all feedback by a specific learner.
     * 
     * @param learner the learner user
     * @return list of feedback from the learner
     */
    List<Feedback> findByLearner(User learner);

    /**
     * Find all feedback by a specific learner ID.
     * 
     * @param learnerId the learner's UUID
     * @return list of feedback from the learner
     */
    List<Feedback> findByLearnerId(UUID learnerId);

    /**
     * Find feedback from a specific learner for a specific course.
     * 
     * @param learnerId the learner's UUID
     * @param courseId the course UUID
     * @return Optional containing the feedback if found
     */
    Optional<Feedback> findByLearnerIdAndCourseId(UUID learnerId, UUID courseId);

    /**
     * Check if feedback exists for a learner-course combination.
     * 
     * @param learnerId the learner's UUID
     * @param courseId the course UUID
     * @return true if feedback exists, false otherwise
     */
    boolean existsByLearnerIdAndCourseId(UUID learnerId, UUID courseId);

    /**
     * Find all feedback with a specific rating.
     * 
     * @param rating the rating value (1-5)
     * @return list of feedback with the specified rating
     */
    List<Feedback> findByRating(Integer rating);

    /**
     * Calculate average rating for a course.
     * 
     * @param courseId the course UUID
     * @return average rating, or 0.0 if no feedback exists
     */
    @Query("SELECT COALESCE(AVG(f.rating), 0.0) FROM Feedback f WHERE f.course.id = :courseId AND f.isVisible = true")
    Double calculateAverageRating(@Param("courseId") UUID courseId);

    /**
     * Count total feedback for a course.
     * 
     * @param courseId the course UUID
     * @param isVisible true for visible feedback
     * @return number of visible feedback entries
     */
    long countByCourseIdAndIsVisible(UUID courseId, Boolean isVisible);
}

