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
 * Controller for instructor-specific operations.
 * 
 * GRASP Pattern: Controller
 * - Routes instructor requests (course management, view enrollments, etc.)
 * - Delegates to service layer for business logic
 * - Only accessible by users with INSTRUCTOR role
 * 
 * MVC Pattern Implementation:
 * - Controller: This class
 * - Model: Services and DTOs
 * - View: Thymeleaf templates in templates/instructor/
 * 
 * Endpoints:
 * - GET  /instructor/dashboard - Instructor dashboard
 * - GET  /instructor/courses - List instructor's courses
 * - GET  /instructor/courses/create - Course creation form
 * - POST /instructor/courses/create - Save new course
 * - GET  /instructor/courses/{id}/edit - Edit course form
 * - POST /instructor/courses/{id}/edit - Update course
 * - POST /instructor/courses/{id}/publish - Publish course
 * - POST /instructor/courses/{id}/archive - Archive course
 * - POST /instructor/courses/{id}/delete - Delete course
 * - GET  /instructor/courses/{id}/enrollments - View course enrollments
 * - GET  /instructor/courses/{id}/feedback - View course feedback
 */
@Slf4j
@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final FeedbackService feedbackService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Display instructor dashboard.
     * 
     * @param userDetails the authenticated user
     * @param model the model for view
     * @return instructor dashboard view
     */
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        var user = userDetailsService.getUserByEmail(userDetails.getUsername());
        log.info("Instructor dashboard for user: {}", user.getId());
        
        // Get instructor's courses
        List<CourseResponseDto> courses = courseService.getCoursesByInstructor(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        model.addAttribute("totalCourses", courses.size());
        
        return "instructor/dashboard";
    }

    /**
     * List all instructor's courses.
     * 
     * @param userDetails the authenticated user
     * @param model the model for view
     * @return courses list view
     */
    @GetMapping("/courses")
    public String listCourses(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        var user = userDetailsService.getUserByEmail(userDetails.getUsername());
        List<CourseResponseDto> courses = courseService.getCoursesByInstructor(user.getId());
        
        model.addAttribute("courses", courses);
        
        return "instructor/courses";
    }

    /**
     * Show course creation form.
     * 
     * @param model the model for view
     * @return course creation form view
     */
    @GetMapping("/courses/create")
    public String showCreateCourseForm(Model model) {
        model.addAttribute("courseCreateDto", new CourseCreateDto());
        return "instructor/course-create";
    }

    /**
     * Create a new course.
     * 
     * @param courseCreateDto the course data
     * @param bindingResult validation results
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to courses list or back to form
     */
    @PostMapping("/courses/create")
    public String createCourse(
            @Valid @ModelAttribute CourseCreateDto courseCreateDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "instructor/course-create";
        }
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            CourseResponseDto course = courseService.createCourse(courseCreateDto, user.getId());
            
            log.info("Course created successfully: {}", course.getId());
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            
            return "redirect:/instructor/courses";
            
        } catch (Exception e) {
            log.error("Course creation failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/instructor/courses/create";
        }
    }

    /**
     * Show course edit form.
     * 
     * @param id the course UUID
     * @param model the model for view
     * @return course edit form view
     */
    @GetMapping("/courses/{id}/edit")
    public String showEditCourseForm(@PathVariable UUID id, Model model) {
        CourseResponseDto course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("courseUpdateDto", new CourseUpdateDto());
        
        return "instructor/course-edit";
    }

    /**
     * Update a course.
     * 
     * @param id the course UUID
     * @param courseUpdateDto the update data
     * @param bindingResult validation results
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to courses list or back to form
     */
    @PostMapping("/courses/{id}/edit")
    public String updateCourse(
            @PathVariable UUID id,
            @Valid @ModelAttribute CourseUpdateDto courseUpdateDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "instructor/course-edit";
        }
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            courseService.updateCourse(id, courseUpdateDto, user.getId());
            
            log.info("Course updated successfully: {}", id);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            
            return "redirect:/instructor/courses";
            
        } catch (Exception e) {
            log.error("Course update failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/instructor/courses/" + id + "/edit";
        }
    }

    /**
     * Publish a course.
     * 
     * @param id the course UUID
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to courses list
     */
    @PostMapping("/courses/{id}/publish")
    public String publishCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            courseService.publishCourse(id, user.getId());
            
            log.info("Course published: {}", id);
            redirectAttributes.addFlashAttribute("success", "Course published successfully!");
            
        } catch (Exception e) {
            log.error("Course publish failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/instructor/courses";
    }

    /**
     * Archive a course.
     * 
     * @param id the course UUID
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to courses list
     */
    @PostMapping("/courses/{id}/archive")
    public String archiveCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            courseService.archiveCourse(id, user.getId());
            
            log.info("Course archived: {}", id);
            redirectAttributes.addFlashAttribute("success", "Course archived successfully!");
            
        } catch (Exception e) {
            log.error("Course archive failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/instructor/courses";
    }

    /**
     * Delete a course.
     * 
     * @param id the course UUID
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to courses list
     */
    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            courseService.deleteCourse(id, user.getId());
            
            log.info("Course deleted: {}", id);
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
            
        } catch (Exception e) {
            log.error("Course delete failed", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/instructor/courses";
    }

    /**
     * View enrollments for a course.
     * 
     * @param id the course UUID
     * @param model the model for view
     * @return enrollments view
     */
    @GetMapping("/courses/{id}/enrollments")
    public String viewCourseEnrollments(@PathVariable UUID id, Model model) {
        log.info("Viewing enrollments for course: {}", id);
        
        CourseResponseDto course = courseService.getCourseById(id);
        List<EnrollmentResponseDto> enrollments = enrollmentService.getEnrollmentsByCourse(id);
        
        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollments);
        
        return "instructor/course-enrollments";
    }

    /**
     * View feedback for a course.
     * 
     * @param id the course UUID
     * @param model the model for view
     * @return feedback view
     */
    @GetMapping("/courses/{id}/feedback")
    public String viewCourseFeedback(@PathVariable UUID id, Model model) {
        log.info("Viewing feedback for course: {}", id);
        
        CourseResponseDto course = courseService.getCourseById(id);
        List<FeedbackResponseDto> feedbackList = feedbackService.getFeedbackByCourse(id);
        
        model.addAttribute("course", course);
        model.addAttribute("feedbackList", feedbackList);
        
        return "instructor/course-feedback";
    }
}

