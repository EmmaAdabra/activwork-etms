package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.Enrollment;
import com.activwork.etms.model.EnrollmentStatus;
import com.activwork.etms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Enrollment entity operations.
 * 
 * Provides data access methods for course enrollments and learner progress tracking.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    /**
     * Find all enrollments for a specific learner.
     * 
     * @param learner the learner user
     * @return list of enrollments for the learner
     */
    List<Enrollment> findByLearner(User learner);

    /**
     * Find all enrollments for a specific learner ID.
     * 
     * @param learnerId the learner's UUID
     * @return list of enrollments for the learner
     */
    List<Enrollment> findByLearnerId(UUID learnerId);

    /**
     * Find all enrollments for a specific course.
     * 
     * @param course the course
     * @return list of enrollments for the course
     */
    List<Enrollment> findByCourse(Course course);

    /**
     * Find all enrollments for a specific course ID.
     * 
     * @param courseId the course UUID
     * @return list of enrollments for the course
     */
    List<Enrollment> findByCourseId(UUID courseId);

    /**
     * Find enrollment for a specific learner and course.
     * Used to check if learner is already enrolled.
     * 
     * @param learnerId the learner's UUID
     * @param courseId the course UUID
     * @return Optional containing the enrollment if found
     */
    Optional<Enrollment> findByLearnerIdAndCourseId(UUID learnerId, UUID courseId);

    /**
     * Find all enrollments by status.
     * 
     * @param status the enrollment status (ACTIVE, COMPLETED, CANCELLED, SUSPENDED)
     * @return list of enrollments with the specified status
     */
    List<Enrollment> findByStatus(EnrollmentStatus status);

    /**
     * Find all enrollments for a learner with specific status.
     * 
     * @param learnerId the learner's UUID
     * @param status the enrollment status
     * @return list of matching enrollments
     */
    List<Enrollment> findByLearnerIdAndStatus(UUID learnerId, EnrollmentStatus status);

    /**
     * Find all enrollments for a course with specific status.
     * 
     * @param courseId the course UUID
     * @param status the enrollment status
     * @return list of matching enrollments
     */
    List<Enrollment> findByCourseIdAndStatus(UUID courseId, EnrollmentStatus status);

    /**
     * Check if a learner is already enrolled in a course.
     * 
     * @param learnerId the learner's UUID
     * @param courseId the course UUID
     * @return true if enrollment exists, false otherwise
     */
    boolean existsByLearnerIdAndCourseId(UUID learnerId, UUID courseId);

    /**
     * Count total enrollments for a course.
     * 
     * @param courseId the course UUID
     * @return number of enrollments
     */
    long countByCourseId(UUID courseId);

    /**
     * Count active enrollments for a course.
     * 
     * @param courseId the course UUID
     * @param status the enrollment status
     * @return number of enrollments with specified status
     */
    long countByCourseIdAndStatus(UUID courseId, EnrollmentStatus status);

    /**
     * Find enrollments eligible for certificate issuance.
     * (Completed status, 100% progress, certificate not yet issued)
     * 
     * @return list of enrollments ready for certificates
     */
    @Query("SELECT e FROM Enrollment e WHERE e.status = 'COMPLETED' AND e.progressPercent = 100 AND e.certificateIssued = false")
    List<Enrollment> findEnrollmentsReadyForCertificate();
}

