package com.activwork.etms.controller;

import com.activwork.etms.dto.*;
import com.activwork.etms.security.CustomUserDetailsService;
import com.activwork.etms.service.CourseService;
import com.activwork.etms.service.EnrollmentService;
import com.activwork.etms.service.FeedbackService;
import com.activwork.etms.service.MaterialService;
import com.activwork.etms.service.FileStorageService;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
    private final MaterialService materialService;
    private final FileStorageService fileStorageService;
    private final CustomUserDetailsService userDetailsService;
    private final com.activwork.etms.service.CourseSectionService courseSectionService;

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
        
        // Calculate completed enrollments count
        long completedCount = allEnrollments.stream()
                .filter(enrollment -> enrollment.getStatus().toString().equals("COMPLETED"))
                .count();
        
        model.addAttribute("user", user);
        model.addAttribute("activeEnrollments", activeEnrollments);
        model.addAttribute("totalEnrollments", allEnrollments.size());
        model.addAttribute("completedCount", completedCount);
        
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
        
        // Calculate enrollment counts by status
        long activeCount = enrollments.stream()
                .filter(enrollment -> enrollment.getStatus().toString().equals("ACTIVE"))
                .count();
        
        long completedCount = enrollments.stream()
                .filter(enrollment -> enrollment.getStatus().toString().equals("COMPLETED"))
                .count();
        
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        
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
        
        // Get course sections with materials
        List<com.activwork.etms.dto.CourseSectionDto> sections = courseSectionService.getSectionsWithMaterialsByCourseId(enrollment.getCourseId());
        
        // Get all course materials (for backward compatibility if no sections)
        List<MaterialResponseDto> materials = materialService.getActiveMaterialsByCourse(enrollment.getCourseId());
        
        model.addAttribute("enrollment", enrollment);
        model.addAttribute("sections", sections);
        model.addAttribute("materials", materials);
        
        return "learner/enrollment-details";
    }
    
    /**
     * Get enrollment progress as JSON (for AJAX updates).
     * 
     * @param id the enrollment UUID
     * @param userDetails the authenticated user
     * @return enrollment data as JSON
     */
    @GetMapping("/enrollments/{id}/progress")
    @ResponseBody
    public ResponseEntity<EnrollmentResponseDto> getEnrollmentProgress(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            EnrollmentResponseDto enrollment = enrollmentService.getEnrollmentById(id);
            
            // Verify user owns this enrollment
            if (!enrollment.getLearnerId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            return ResponseEntity.ok(enrollment);
            
        } catch (Exception e) {
            log.error("Failed to fetch enrollment progress", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all material progress for an enrollment.
     * 
     * @param id the enrollment UUID
     * @param userDetails the authenticated user
     * @return list of material progress
     */
    @GetMapping("/enrollments/{id}/materials/progress")
    @ResponseBody
    public ResponseEntity<List<MaterialProgressDto>> getEnrollmentMaterialProgress(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            EnrollmentResponseDto enrollment = enrollmentService.getEnrollmentById(id);
            
            // Verify user owns this enrollment
            if (!enrollment.getLearnerId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            List<MaterialProgressDto> progressList = materialService.getMaterialProgressByEnrollment(id);
            return ResponseEntity.ok(progressList);
            
        } catch (Exception e) {
            log.error("Failed to fetch material progress for enrollment: {}", id, e);
            return ResponseEntity.status(500).build();
        }
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
            RedirectAttributes redirectAttributes,
            Model model) {
        
        log.debug("Feedback submission - Path Course ID: {}, DTO Course ID: {}, Rating: {}, Comment: {}", 
                 id, feedbackDto.getCourseId(), feedbackDto.getRating(), feedbackDto.getComment());
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in feedback submission: {}", bindingResult.getAllErrors());
            // Add course data to model for validation error display
            CourseResponseDto course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            return "learner/feedback-form";
        }
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            // Always set courseId from path variable for security
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

    /**
     * View/download a material file.
     * 
     * @param id the material UUID
     * @param userDetails the authenticated user
     * @return material file as response
     */
    @GetMapping("/materials/{id}/view")
    public ResponseEntity<Resource> viewMaterial(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            MaterialResponseDto material = materialService.getMaterialById(id);
            
            // Verify user is enrolled in the course
            boolean isEnrolled = enrollmentService.isLearnerEnrolled(user.getId(), material.getCourseId());
            if (!isEnrolled) {
                return ResponseEntity.status(403).build();
            }
            
            // Load file resource
            Resource resource = fileStorageService.loadFileAsResource(material.getFilename(), "material");
            
            // Increment view count
            materialService.incrementViewCount(id);
            
            // Determine content type
            String contentType = material.getMimeType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + material.getOriginalFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Error viewing material: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Download a material file.
     * 
     * @param id the material UUID
     * @param userDetails the authenticated user
     * @return material file as download
     */
    @GetMapping("/materials/{id}/download")
    public ResponseEntity<Resource> downloadMaterial(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            MaterialResponseDto material = materialService.getMaterialById(id);
            
            // Verify user is enrolled in the course
            boolean isEnrolled = enrollmentService.isLearnerEnrolled(user.getId(), material.getCourseId());
            if (!isEnrolled) {
                return ResponseEntity.status(403).build();
            }
            
            // PREVENT video downloads
            if ("VIDEO".equals(material.getMaterialType().toString())) {
                log.warn("Attempt to download video material blocked: {}", id);
                return ResponseEntity.status(403).build();
            }
            
            // Check if material is downloadable
            if (!material.getIsDownloadable()) {
                return ResponseEntity.status(403).build();
            }
            
            // Load file resource
            Resource resource = fileStorageService.loadFileAsResource(material.getFilename(), "material");
            
            // Increment download count
            materialService.incrementDownloadCount(id);
            
            // Determine content type
            String contentType = material.getMimeType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + material.getOriginalFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Error downloading material: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get material progress for a learner.
     * 
     * @param materialId the material UUID
     * @param userDetails the authenticated user
     * @return material progress data
     */
    @GetMapping("/materials/{id}/progress")
    public ResponseEntity<MaterialProgressDto> getMaterialProgress(
            @PathVariable("id") UUID materialId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            MaterialResponseDto material = materialService.getMaterialById(materialId);
            
            // Verify user is enrolled in the course
            boolean isEnrolled = enrollmentService.isLearnerEnrolled(user.getId(), material.getCourseId());
            if (!isEnrolled) {
                return ResponseEntity.status(403).build();
            }
            
            // Get or create material progress
            MaterialProgressDto progress = materialService.getOrCreateMaterialProgress(
                enrollmentService.getEnrollmentByLearnerAndCourse(user.getId(), material.getCourseId()).getId(),
                materialId
            );
            
            return ResponseEntity.ok(progress);
            
        } catch (Exception e) {
            log.error("Error getting material progress: {}", materialId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update material progress for a learner.
     * 
     * @param progressDto the progress data
     * @param userDetails the authenticated user
     * @return updated progress data
     */
    @PostMapping("/materials/progress")
    public ResponseEntity<MaterialProgressDto> updateMaterialProgress(
            @RequestBody MaterialProgressUpdateDto progressDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            MaterialResponseDto material = materialService.getMaterialById(progressDto.getMaterialId());
            
            // Verify user is enrolled in the course
            boolean isEnrolled = enrollmentService.isLearnerEnrolled(user.getId(), material.getCourseId());
            if (!isEnrolled) {
                return ResponseEntity.status(403).build();
            }
            
            // Update material progress
            MaterialProgressDto updatedProgress = materialService.updateMaterialProgress(
                enrollmentService.getEnrollmentByLearnerAndCourse(user.getId(), material.getCourseId()).getId(),
                progressDto
            );
            
            return ResponseEntity.ok(updatedProgress);
            
        } catch (Exception e) {
            log.error("Error updating material progress", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

