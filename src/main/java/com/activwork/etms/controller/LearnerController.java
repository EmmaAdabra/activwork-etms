package com.activwork.etms.controller;

import com.activwork.etms.dto.*;
import com.activwork.etms.security.CustomUserDetailsService;
import com.activwork.etms.service.CourseService;
import com.activwork.etms.service.EnrollmentService;
import com.activwork.etms.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controller for learner-specific operations.
 * 
 * GRASP Pattern: Controller
 * - Routes learner requests (enroll, view progress, submit feedback)
 * - Delegates to service layer for business logic
 * - Only accessible by users with LEARNER role
 * 
 * MVC Pattern Implementation:
 * - Controller: This class
 * - Model: Services and DTOs
 * - View: Thymeleaf templates in templates/learner/
 * 
 * Endpoints:
 * - GET  /learner/dashboard - Learner dashboard with enrollments
 * - POST /courses/{id}/enroll - Enroll in a course
 * - GET  /learner/enrollments - List all enrollments
 * - GET  /learner/enrollments/{id} - View enrollment details
 * - POST /learner/enrollments/{id}/cancel - Cancel enrollment
 * - GET  /courses/{id}/feedback/submit - Feedback form
 * - POST /courses/{id}/feedback/submit - Submit feedback
 */
@Slf4j
@Controller
@RequestMapping("/learner")
@RequiredArgsConstructor
public class LearnerController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final FeedbackService feedbackService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Display learner dashboard.
     * 
     * @param userDetails the authenticated user
     * @param model the model for view
     * @return learner dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        var user = userDetailsService.getUserByEmail(userDetails.getUsername());
        log.info("Learner dashboard for user: {}", user.getId());
        
        // Get learner's enrollments
        List<EnrollmentResponseDto> activeEnrollments = enrollmentService.getActiveEnrollmentsByLearner(user.getId());
        List<EnrollmentResponseDto> allEnrollments = enrollmentService.getEnrollmentsByLearner(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("activeEnrollments", activeEnrollments);
        model.addAttribute("totalEnrollments", allEnrollments.size());
        
        return "learner/dashboard";
    }

    /**
     * Enroll in a course.
     * 
     * @param id the course UUID
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to course or dashboard
     */
    @PostMapping("/courses/{id}/enroll")
    public String enrollInCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            
            EnrollmentRequestDto enrollmentRequest = new EnrollmentRequestDto();
            enrollmentRequest.setLearnerId(user.getId());
            enrollmentRequest.setCourseId(id);
            
            EnrollmentResponseDto enrollment = enrollmentService.enrollLearner(enrollmentRequest);
            
            log.info("Enrollment successful: {}", enrollment.getId());
            redirectAttributes.addFlashAttribute("success", "Successfully enrolled in the course!");
            
            return "redirect:/learner/dashboard";
            
        } catch (Exception e) {
            log.error("Enrollment failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/" + id;
        }
    }

    /**
     * List all learner's enrollments.
     * 
     * @param userDetails the authenticated user
     * @param model the model for view
     * @return enrollments list view
     */
    @GetMapping("/enrollments")
    public String listEnrollments(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        var user = userDetailsService.getUserByEmail(userDetails.getUsername());
        List<EnrollmentResponseDto> enrollments = enrollmentService.getEnrollmentsByLearner(user.getId());
        
        model.addAttribute("enrollments", enrollments);
        
        return "learner/enrollments";
    }

    /**
     * View enrollment details.
     * 
     * @param id the enrollment UUID
     * @param model the model for view
     * @return enrollment details view
     */
    @GetMapping("/enrollments/{id}")
    public String viewEnrollment(@PathVariable UUID id, Model model) {
        log.info("Viewing enrollment: {}", id);
        
        EnrollmentResponseDto enrollment = enrollmentService.getEnrollmentById(id);
        
        model.addAttribute("enrollment", enrollment);
        
        return "learner/enrollment-details";
    }

    /**
     * Cancel enrollment.
     * 
     * @param id the enrollment UUID
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to enrollments list
     */
    @PostMapping("/enrollments/{id}/cancel")
    public String cancelEnrollment(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            enrollmentService.cancelEnrollment(id, user.getId());
            
            log.info("Enrollment canceled: {}", id);
            redirectAttributes.addFlashAttribute("success", "Enrollment canceled successfully!");
            
        } catch (Exception e) {
            log.error("Enrollment cancellation failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/learner/enrollments";
    }

    /**
     * Show feedback submission form.
     * 
     * @param id the course UUID
     * @param model the model for view
     * @return feedback form view
     */
    @GetMapping("/courses/{id}/feedback")
    public String showFeedbackForm(@PathVariable UUID id, Model model) {
        CourseResponseDto course = courseService.getCourseById(id);
        
        model.addAttribute("course", course);
        model.addAttribute("feedbackDto", new FeedbackDto());
        
        return "learner/feedback-form";
    }

    /**
     * Submit course feedback.
     * 
     * @param id the course UUID
     * @param feedbackDto the feedback data
     * @param bindingResult validation results
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to enrollments or back to form
     */
    @PostMapping("/courses/{id}/feedback")
    public String submitFeedback(
            @PathVariable UUID id,
            @Valid @ModelAttribute FeedbackDto feedbackDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "learner/feedback-form";
        }
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            feedbackDto.setCourseId(id);
            
            feedbackService.submitFeedback(feedbackDto, user.getId());
            
            log.info("Feedback submitted for course: {}", id);
            redirectAttributes.addFlashAttribute("success", "Thank you for your feedback!");
            
            return "redirect:/learner/enrollments";
            
        } catch (Exception e) {
            log.error("Feedback submission failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/learner/courses/" + id + "/feedback";
        }
    }
}

