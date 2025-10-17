package com.activwork.etms.service;

import com.activwork.etms.dto.EnrollmentRequestDto;
import com.activwork.etms.dto.EnrollmentResponseDto;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.*;
import com.activwork.etms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Enrollment entity operations.
 * 
 * GRASP Pattern: Information Expert
 * - Expert on enrollment business rules and validation
 * - Knows when a learner can enroll in a course
 * - Responsible for progress tracking logic
 * 
 * Business Rules Enforced:
 * 1. Only learners can enroll in courses
 * 2. Cannot enroll in the same course twice
 * 3. Course must be published and active
 * 4. Enrollment capacity must not be exceeded
 * 5. Enrollment deadline must not have passed
 * 6. Prerequisites must be met (if applicable)
 * 
 * Architecture:
 * - Uses multiple repositories (Enrollment, Course, User, MaterialProgress)
 * - Throws domain exceptions for business rule violations
 * - Manages transactions for data consistency
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;

    /**
     * Enroll a learner in a course.
     * Validates all enrollment business rules before creating enrollment.
     * 
     * @param enrollmentRequestDto enrollment request data
     * @return created enrollment response
     * @throws ResourceNotFoundException if learner or course not found
     * @throws IllegalArgumentException if business rules violated
     */
    @Transactional
    public EnrollmentResponseDto enrollLearner(EnrollmentRequestDto enrollmentRequestDto) {
        UUID learnerId = enrollmentRequestDto.getLearnerId();
        UUID courseId = enrollmentRequestDto.getCourseId();
        
        log.info("Enrolling learner {} in course {}", learnerId, courseId);
        
        // Get learner
        User learner = userRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", learnerId));
        
        // Business rule: Only learners can enroll
        if (!learner.isLearner()) {
            throw new IllegalArgumentException("Only learners can enroll in courses");
        }
        
        // Get course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Business rule: Cannot enroll in the same course twice
        if (enrollmentRepository.existsByLearnerIdAndCourseId(learnerId, courseId)) {
            throw new IllegalArgumentException("Already enrolled in this course");
        }
        
        // Business rule: Course must be available for enrollment
        if (!course.isAvailableForEnrollment()) {
            throw new IllegalArgumentException("Course is not available for enrollment");
        }
        
        // Business rule: Enrollment deadline must not have passed
        if (course.isEnrollmentDeadlinePassed()) {
            throw new IllegalArgumentException("Enrollment deadline has passed");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setLearner(learner);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setProgressPercent(BigDecimal.ZERO);
        enrollment.setNotes(enrollmentRequestDto.getNotes());
        
        // Initialize material counts
        long totalMaterials = materialRepository.countByCourseIdAndIsActive(courseId, true);
        enrollment.setTotalMaterials((int) totalMaterials);
        enrollment.setCompletedMaterials(0);
        enrollment.setTimeSpentMinutes(0);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        log.info("Learner enrolled successfully. Enrollment ID: {}", savedEnrollment.getId());
        return EnrollmentResponseDto.fromEntity(savedEnrollment);
    }

    /**
     * Get enrollment by ID.
     * 
     * @param enrollmentId the enrollment UUID
     * @return enrollment response
     * @throws ResourceNotFoundException if enrollment not found
     */
    public EnrollmentResponseDto getEnrollmentById(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        
        return EnrollmentResponseDto.fromEntity(enrollment);
    }

    /**
     * Get all enrollments for a learner.
     * 
     * @param learnerId the learner UUID
     * @return list of learner's enrollments
     */
    public List<EnrollmentResponseDto> getEnrollmentsByLearner(UUID learnerId) {
        List<Enrollment> enrollments = enrollmentRepository.findByLearnerId(learnerId);
        return enrollments.stream()
                .map(EnrollmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get active enrollments for a learner.
     * 
     * @param learnerId the learner UUID
     * @return list of active enrollments
     */
    public List<EnrollmentResponseDto> getActiveEnrollmentsByLearner(UUID learnerId) {
        List<Enrollment> enrollments = enrollmentRepository.findByLearnerIdAndStatus(
                learnerId, 
                EnrollmentStatus.ACTIVE
        );
        return enrollments.stream()
                .map(EnrollmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all enrollments for a course.
     * 
     * @param courseId the course UUID
     * @return list of course enrollments
     */
    public List<EnrollmentResponseDto> getEnrollmentsByCourse(UUID courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream()
                .map(EnrollmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update enrollment progress.
     * Note: Progress is also updated automatically by database trigger
     * when MaterialProgress records change.
     * 
     * @param enrollmentId the enrollment UUID
     * @param progressPercent the new progress percentage
     * @return updated enrollment
     */
    @Transactional
    public EnrollmentResponseDto updateProgress(UUID enrollmentId, BigDecimal progressPercent) {
        log.info("Updating progress for enrollment: {}", enrollmentId);
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        
        enrollment.setProgressPercent(progressPercent);
        
        // Auto-complete if 100%
        if (progressPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            enrollment.complete();
        }
        
        Enrollment updated = enrollmentRepository.save(enrollment);
        return EnrollmentResponseDto.fromEntity(updated);
    }
    
    /**
     * Update enrollment progress with material counts.
     * 
     * @param enrollmentId the enrollment UUID
     * @param progressPercent the new progress percentage
     * @param completedMaterials number of completed materials
     * @param totalMaterials total number of materials
     * @return updated enrollment
     */
    @Transactional
    public EnrollmentResponseDto updateProgress(UUID enrollmentId, BigDecimal progressPercent, 
                                                int completedMaterials, int totalMaterials) {
        log.info("Updating progress for enrollment: {} - {}% ({}/{})", 
                enrollmentId, progressPercent, completedMaterials, totalMaterials);
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        
        enrollment.setProgressPercent(progressPercent);
        enrollment.setCompletedMaterials(completedMaterials);
        enrollment.setTotalMaterials(totalMaterials);
        
        // Auto-complete if 100%, revert to ACTIVE if less than 100%
        if (progressPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            if (!EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {
                enrollment.complete();
                log.info("🎉 Course COMPLETED for enrollment: {}", enrollmentId);
            }
        } else {
            // If progress drops below 100%, revert to ACTIVE status
            if (EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollment.setCompletionDate(null);
                log.info("↩️ Course status reverted to ACTIVE for enrollment: {}", enrollmentId);
            }
        }
        
        Enrollment updated = enrollmentRepository.save(enrollment);
        log.info("✅ Enrollment updated: {} - Progress: {}%, Materials: {}/{}, Status: {}", 
                enrollmentId, progressPercent, completedMaterials, totalMaterials, updated.getStatus());
        
        return EnrollmentResponseDto.fromEntity(updated);
    }

    /**
     * Cancel enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @param learnerId the learner canceling (for authorization)
     * @return canceled enrollment
     */
    @Transactional
    public EnrollmentResponseDto cancelEnrollment(UUID enrollmentId, UUID learnerId) {
        log.info("Canceling enrollment: {}", enrollmentId);
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        
        // Business rule: Only the enrolled learner can cancel
        if (!enrollment.getLearner().getId().equals(learnerId)) {
            throw new IllegalArgumentException("Only the enrolled learner can cancel this enrollment");
        }
        
        // Business rule: Cannot cancel completed enrollments
        if (enrollment.isCompleted()) {
            throw new IllegalArgumentException("Cannot cancel a completed enrollment");
        }
        
        enrollment.cancel();
        Enrollment canceled = enrollmentRepository.save(enrollment);
        
        log.info("Enrollment canceled successfully: {}", enrollmentId);
        return EnrollmentResponseDto.fromEntity(canceled);
    }

    /**
     * Check if learner is enrolled in a course.
     * 
     * @param learnerId the learner UUID
     * @param courseId the course UUID
     * @return true if enrolled
     */
    public boolean isLearnerEnrolled(UUID learnerId, UUID courseId) {
        return enrollmentRepository.existsByLearnerIdAndCourseId(learnerId, courseId);
    }

    /**
     * Get enrollment by learner and course.
     * 
     * @param learnerId the learner UUID
     * @param courseId the course UUID
     * @return enrollment response
     * @throws ResourceNotFoundException if enrollment not found
     */
    public EnrollmentResponseDto getEnrollmentByLearnerAndCourse(UUID learnerId, UUID courseId) {
        Enrollment enrollment = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", UUID.randomUUID()));
        
        return EnrollmentResponseDto.fromEntity(enrollment);
    }

    /**
     * Get enrollments ready for certificate issuance.
     * 
     * @return list of enrollments ready for certificates
     */
    public List<EnrollmentResponseDto> getEnrollmentsReadyForCertificate() {
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsReadyForCertificate();
        return enrollments.stream()
                .map(EnrollmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Issue certificate for completed enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @param certificateUrl the URL to the generated certificate
     * @return updated enrollment
     */
    @Transactional
    public EnrollmentResponseDto issueCertificate(UUID enrollmentId, String certificateUrl) {
        log.info("Issuing certificate for enrollment: {}", enrollmentId);
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        
        // Business rule: Can only issue certificate for completed enrollments
        if (!enrollment.canIssueCertificate()) {
            throw new IllegalArgumentException("Certificate can only be issued for completed enrollments");
        }
        
        enrollment.setCertificateIssued(true);
        enrollment.setCertificateUrl(certificateUrl);
        
        Enrollment updated = enrollmentRepository.save(enrollment);
        
        log.info("Certificate issued successfully for enrollment: {}", enrollmentId);
        return EnrollmentResponseDto.fromEntity(updated);
    }
}

