package com.activwork.etms.service;

import com.activwork.etms.dto.FeedbackDto;
import com.activwork.etms.dto.FeedbackResponseDto;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.*;
import com.activwork.etms.repository.CourseRepository;
import com.activwork.etms.repository.EnrollmentRepository;
import com.activwork.etms.repository.FeedbackRepository;
import com.activwork.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Feedback entity operations.
 * 
 * GRASP Pattern: Information Expert
 * - Expert on feedback and rating business rules
 * - Knows when feedback can be submitted
 * - Handles rating calculations (delegated to database trigger)
 * 
 * Business Rules Enforced:
 * 1. Only enrolled learners can submit feedback
 * 2. One feedback per learner per course
 * 3. Rating must be 1-5
 * 4. Feedback visibility can be moderated
 * 
 * Note: Average rating calculation is handled by database trigger
 * (trigger_update_course_rating) for consistency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Submit feedback for a course.
     * 
     * @param feedbackDto feedback data
     * @param learnerId the learner submitting feedback
     * @return created feedback response
     * @throws ResourceNotFoundException if learner or course not found
     * @throws IllegalArgumentException if business rules violated
     */
    @Transactional
    public FeedbackResponseDto submitFeedback(FeedbackDto feedbackDto, UUID learnerId) {
        log.info("Submitting feedback for course {} by learner {}", feedbackDto.getCourseId(), learnerId);
        
        // Get learner
        User learner = userRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", learnerId));
        
        // Business rule: Only learners can submit feedback
        if (!learner.isLearner()) {
            throw new IllegalArgumentException("Only learners can submit feedback");
        }
        
        // Get course
        Course course = courseRepository.findById(feedbackDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", feedbackDto.getCourseId()));
        
        // Business rule: Learner must be enrolled in the course
        if (!enrollmentRepository.existsByLearnerIdAndCourseId(learnerId, feedbackDto.getCourseId())) {
            throw new IllegalArgumentException("You must be enrolled in the course to submit feedback");
        }
        
        // Business rule: One feedback per learner per course
        if (feedbackRepository.existsByLearnerIdAndCourseId(learnerId, feedbackDto.getCourseId())) {
            throw new IllegalArgumentException("You have already submitted feedback for this course");
        }
        
        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setLearner(learner);
        feedback.setCourse(course);
        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());
        feedback.setIsVisible(true);
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        
        // Note: Database trigger automatically updates course average_rating
        
        log.info("Feedback submitted successfully. ID: {}", savedFeedback.getId());
        return FeedbackResponseDto.fromEntity(savedFeedback);
    }

    /**
     * Get all feedback for a course.
     * 
     * @param courseId the course UUID
     * @return list of feedback for the course
     */
    public List<FeedbackResponseDto> getFeedbackByCourse(UUID courseId) {
        List<Feedback> feedbackList = feedbackRepository.findByCourseId(courseId);
        return feedbackList.stream()
                .map(FeedbackResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all visible feedback for a course (for public display).
     * 
     * @param courseId the course UUID
     * @return list of visible feedback
     */
    public List<FeedbackResponseDto> getVisibleFeedbackByCourse(UUID courseId) {
        List<Feedback> feedbackList = feedbackRepository.findByCourseIdAndIsVisible(courseId, true);
        return feedbackList.stream()
                .map(FeedbackResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all feedback submitted by a learner.
     * 
     * @param learnerId the learner UUID
     * @return list of learner's feedback
     */
    public List<FeedbackResponseDto> getFeedbackByLearner(UUID learnerId) {
        List<Feedback> feedbackList = feedbackRepository.findByLearnerId(learnerId);
        return feedbackList.stream()
                .map(FeedbackResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Hide feedback (moderation function).
     * 
     * @param feedbackId the feedback UUID
     * @param instructorId the instructor moderating (must be course instructor)
     * @return updated feedback
     * @throws ResourceNotFoundException if feedback not found
     * @throws IllegalArgumentException if not authorized
     */
    @Transactional
    public FeedbackResponseDto hideFeedback(UUID feedbackId, UUID instructorId) {
        log.info("Hiding feedback: {} by instructor: {}", feedbackId, instructorId);
        
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", feedbackId));
        
        // Business rule: Only course instructor can moderate feedback
        if (!feedback.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can moderate feedback");
        }
        
        feedback.hide();
        Feedback updated = feedbackRepository.save(feedback);
        
        // Note: Database trigger will recalculate course rating
        
        log.info("Feedback hidden successfully: {}", feedbackId);
        return FeedbackResponseDto.fromEntity(updated);
    }

    /**
     * Show feedback (un-moderate).
     * 
     * @param feedbackId the feedback UUID
     * @param instructorId the instructor moderating
     * @return updated feedback
     */
    @Transactional
    public FeedbackResponseDto showFeedback(UUID feedbackId, UUID instructorId) {
        log.info("Showing feedback: {} by instructor: {}", feedbackId, instructorId);
        
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", feedbackId));
        
        if (!feedback.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can moderate feedback");
        }
        
        feedback.show();
        Feedback updated = feedbackRepository.save(feedback);
        
        log.info("Feedback shown successfully: {}", feedbackId);
        return FeedbackResponseDto.fromEntity(updated);
    }

    /**
     * Calculate average rating for a course.
     * Uses repository query for calculation.
     * 
     * @param courseId the course UUID
     * @return average rating (0.0 if no feedback)
     */
    public Double getAverageRating(UUID courseId) {
        return feedbackRepository.calculateAverageRating(courseId);
    }
}

