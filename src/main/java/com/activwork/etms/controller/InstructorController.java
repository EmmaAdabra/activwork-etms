package com.activwork.etms.controller;

import com.activwork.etms.dto.*;
import com.activwork.etms.model.MaterialType;
import com.activwork.etms.security.CustomUserDetailsService;
import com.activwork.etms.service.CourseService;
import com.activwork.etms.service.EnrollmentService;
import com.activwork.etms.service.FeedbackService;
import com.activwork.etms.service.FileStorageService;
import com.activwork.etms.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

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
    private final MaterialService materialService;
    private final FileStorageService fileStorageService;
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
        
        // Calculate course counts by status
        long publishedCount = courses.stream()
                .filter(course -> course.getStatus() == com.activwork.etms.model.CourseStatus.PUBLISHED)
                .count();
        
        long draftCount = courses.stream()
                .filter(course -> course.getStatus() == com.activwork.etms.model.CourseStatus.DRAFT)
                .count();
        
        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        model.addAttribute("totalCourses", courses.size());
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
        
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
        
        // Calculate course counts by status
        long publishedCount = courses.stream()
                .filter(course -> course.getStatus() == com.activwork.etms.model.CourseStatus.PUBLISHED)
                .count();
        
        long draftCount = courses.stream()
                .filter(course -> course.getStatus() == com.activwork.etms.model.CourseStatus.DRAFT)
                .count();
        
        long archivedCount = courses.stream()
                .filter(course -> course.getStatus() == com.activwork.etms.model.CourseStatus.ARCHIVED)
                .count();
        
        model.addAttribute("courses", courses);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("archivedCount", archivedCount);
        
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
     * @param bannerFile the course banner image (optional)
     * @param bindingResult validation results
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect to courses list or back to form
     */
    @PostMapping("/courses/create")
    public String createCourse(
            @Valid @ModelAttribute CourseCreateDto courseCreateDto,
            @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "instructor/course-create";
        }
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            
            // Handle banner upload if provided
            if (bannerFile != null && !bannerFile.isEmpty()) {
                // Validate it's an image
                if (!fileStorageService.isImageFile(bannerFile)) {
                    model.addAttribute("error", "Banner must be an image file (JPG, PNG, etc.)");
                    return "instructor/course-create";
                }
                
                String bannerFilename = fileStorageService.storeBanner(bannerFile);
                String bannerUrl = "/uploads/banners/" + bannerFilename;
                courseCreateDto.setThumbnailUrl(bannerUrl);
            }
            
            CourseResponseDto course = courseService.createCourse(courseCreateDto, user.getId());
            
            log.info("Course created successfully: {}", course.getId());
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            redirectAttributes.addFlashAttribute("newCourseId", course.getId());
            
            return "redirect:/instructor/courses";
            
        } catch (Exception e) {
            log.error("Course creation failed", e);
            model.addAttribute("error", e.getMessage());
            return "instructor/course-create";
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
        List<MaterialResponseDto> materials = materialService.getMaterialsByCourse(id);
        
        // Create DTO with existing course data
        CourseUpdateDto courseUpdateDto = new CourseUpdateDto();
        courseUpdateDto.setTitle(course.getTitle());
        courseUpdateDto.setSummary(course.getSummary());
        courseUpdateDto.setDescription(course.getDescription());
        courseUpdateDto.setCategory(course.getCategory());
        courseUpdateDto.setDifficultyLevel(course.getDifficultyLevel());
        courseUpdateDto.setDurationHours(course.getDurationHours());
        courseUpdateDto.setMaxEnrollments(course.getMaxEnrollments());
        courseUpdateDto.setPrice(course.getPrice());
        courseUpdateDto.setThumbnailUrl(course.getThumbnailUrl());
        courseUpdateDto.setVideoPreviewUrl(course.getVideoPreviewUrl());
        courseUpdateDto.setPrerequisites(course.getPrerequisites());
        courseUpdateDto.setLearningObjectives(course.getLearningObjectives());
        courseUpdateDto.setTags(course.getTags());
        courseUpdateDto.setStartDate(course.getStartDate());
        courseUpdateDto.setEndDate(course.getEndDate());
        courseUpdateDto.setEnrollmentDeadline(course.getEnrollmentDeadline());
        courseUpdateDto.setIsFeatured(course.getIsFeatured());
        courseUpdateDto.setIsActive(course.getIsActive());
        
        model.addAttribute("course", course);
        model.addAttribute("courseUpdateDto", courseUpdateDto);
        model.addAttribute("materials", materials);
        
        return "instructor/course-edit";
    }

    /**
     * Update a course.
     * 
     * @param id the course UUID
     * @param courseUpdateDto the update data
     * @param bannerFile the course banner image (optional)
     * @param bindingResult validation results
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @param model the model for view
     * @return redirect to courses list or back to form
     */
    @PostMapping("/courses/{id}/edit")
    public String updateCourse(
            @PathVariable UUID id,
            @Valid @ModelAttribute CourseUpdateDto courseUpdateDto,
            @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpServletRequest request) {
        
        if (bindingResult.hasErrors()) {
            // Check if this is an AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                // Return error response for AJAX
                throw new RuntimeException("Validation failed: " + bindingResult.getAllErrors().toString());
            }
            
            // Reload course and materials for display
            CourseResponseDto course = courseService.getCourseById(id);
            List<MaterialResponseDto> materials = materialService.getMaterialsByCourse(id);
            
            // Populate DTO with existing course data
            courseUpdateDto.setTitle(course.getTitle());
            courseUpdateDto.setSummary(course.getSummary());
            courseUpdateDto.setDescription(course.getDescription());
            courseUpdateDto.setCategory(course.getCategory());
            courseUpdateDto.setDifficultyLevel(course.getDifficultyLevel());
            courseUpdateDto.setDurationHours(course.getDurationHours());
            courseUpdateDto.setMaxEnrollments(course.getMaxEnrollments());
            courseUpdateDto.setPrice(course.getPrice());
            courseUpdateDto.setThumbnailUrl(course.getThumbnailUrl());
            courseUpdateDto.setVideoPreviewUrl(course.getVideoPreviewUrl());
            courseUpdateDto.setPrerequisites(course.getPrerequisites());
            courseUpdateDto.setLearningObjectives(course.getLearningObjectives());
            courseUpdateDto.setTags(course.getTags());
            courseUpdateDto.setStartDate(course.getStartDate());
            courseUpdateDto.setEndDate(course.getEndDate());
            courseUpdateDto.setEnrollmentDeadline(course.getEnrollmentDeadline());
            courseUpdateDto.setIsFeatured(course.getIsFeatured());
            courseUpdateDto.setIsActive(course.getIsActive());
            
            model.addAttribute("course", course);
            model.addAttribute("courseUpdateDto", courseUpdateDto);
            model.addAttribute("materials", materials);
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors below.");
            return "redirect:/instructor/courses/" + id + "/edit";
        }
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            
            // Handle banner upload if provided
            if (bannerFile != null && !bannerFile.isEmpty()) {
                // Validate it's an image
                if (!fileStorageService.isImageFile(bannerFile)) {
                    // Check if this is an AJAX request
                    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                        throw new RuntimeException("Banner must be an image file (JPG, PNG, etc.)");
                    }
                    
                    CourseResponseDto course = courseService.getCourseById(id);
                    List<MaterialResponseDto> materials = materialService.getMaterialsByCourse(id);
                    
                    // Populate DTO with existing course data
                    courseUpdateDto.setTitle(course.getTitle());
                    courseUpdateDto.setSummary(course.getSummary());
                    courseUpdateDto.setDescription(course.getDescription());
                    courseUpdateDto.setCategory(course.getCategory());
                    courseUpdateDto.setDifficultyLevel(course.getDifficultyLevel());
                    courseUpdateDto.setDurationHours(course.getDurationHours());
                    courseUpdateDto.setMaxEnrollments(course.getMaxEnrollments());
                    courseUpdateDto.setPrice(course.getPrice());
                    courseUpdateDto.setThumbnailUrl(course.getThumbnailUrl());
                    courseUpdateDto.setVideoPreviewUrl(course.getVideoPreviewUrl());
                    courseUpdateDto.setPrerequisites(course.getPrerequisites());
                    courseUpdateDto.setLearningObjectives(course.getLearningObjectives());
                    courseUpdateDto.setTags(course.getTags());
                    courseUpdateDto.setStartDate(course.getStartDate());
                    courseUpdateDto.setEndDate(course.getEndDate());
                    courseUpdateDto.setEnrollmentDeadline(course.getEnrollmentDeadline());
                    courseUpdateDto.setIsFeatured(course.getIsFeatured());
                    courseUpdateDto.setIsActive(course.getIsActive());
                    
                    model.addAttribute("course", course);
                    model.addAttribute("courseUpdateDto", courseUpdateDto);
                    model.addAttribute("materials", materials);
                    redirectAttributes.addFlashAttribute("error", "Banner must be an image file (JPG, PNG, etc.)");
                    return "redirect:/instructor/courses/" + id + "/edit";
                }
                
                String bannerFilename = fileStorageService.storeBanner(bannerFile);
                String bannerUrl = "/uploads/banners/" + bannerFilename;
                courseUpdateDto.setThumbnailUrl(bannerUrl);
            }
            
            courseService.updateCourse(id, courseUpdateDto, user.getId());
            
            log.info("Course updated successfully: {}", id);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            
            return "redirect:/instructor/courses";
            
        } catch (Exception e) {
            log.error("Course update failed", e);
            
            // Check if this is an AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                throw e; // Re-throw for AJAX error handling
            }
            
            // Reload course and materials for display
            CourseResponseDto course = courseService.getCourseById(id);
            List<MaterialResponseDto> materials = materialService.getMaterialsByCourse(id);
            
            // Populate DTO with existing course data
            courseUpdateDto.setTitle(course.getTitle());
            courseUpdateDto.setSummary(course.getSummary());
            courseUpdateDto.setDescription(course.getDescription());
            courseUpdateDto.setCategory(course.getCategory());
            courseUpdateDto.setDifficultyLevel(course.getDifficultyLevel());
            courseUpdateDto.setDurationHours(course.getDurationHours());
            courseUpdateDto.setMaxEnrollments(course.getMaxEnrollments());
            courseUpdateDto.setPrice(course.getPrice());
            courseUpdateDto.setThumbnailUrl(course.getThumbnailUrl());
            courseUpdateDto.setVideoPreviewUrl(course.getVideoPreviewUrl());
            courseUpdateDto.setPrerequisites(course.getPrerequisites());
            courseUpdateDto.setLearningObjectives(course.getLearningObjectives());
            courseUpdateDto.setTags(course.getTags());
            courseUpdateDto.setStartDate(course.getStartDate());
            courseUpdateDto.setEndDate(course.getEndDate());
            courseUpdateDto.setEnrollmentDeadline(course.getEnrollmentDeadline());
            courseUpdateDto.setIsFeatured(course.getIsFeatured());
            courseUpdateDto.setIsActive(course.getIsActive());
            
            model.addAttribute("course", course);
            model.addAttribute("courseUpdateDto", courseUpdateDto);
            model.addAttribute("materials", materials);
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

    /**
     * Upload course material.
     * 
     * @param courseId the course UUID
     * @param materialFile the material file
     * @param materialType the type of material
     * @param description optional description
     * @param isRequired whether material is required
     * @param isDownloadable whether material can be downloaded
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect back to course edit page
     */
    @PostMapping("/courses/{courseId}/materials/upload")
    public String uploadMaterial(
            @PathVariable("courseId") UUID courseId,
            @RequestParam("materialFile") MultipartFile materialFile,
            @RequestParam(value = "materialType", required = false) MaterialType materialType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isRequired", defaultValue = "false") Boolean isRequired,
            @RequestParam(value = "isDownloadable", defaultValue = "true") Boolean isDownloadable,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            
            // Auto-detect material type if not provided
            if (materialType == null) {
                materialType = materialService.determineMaterialType(materialFile);
            }
            
            MaterialResponseDto material = materialService.uploadMaterial(
                courseId,
                materialFile,
                materialType,
                description,
                isRequired,
                isDownloadable,
                user.getId()
            );
            
            log.info("Material uploaded successfully: {} for course: {}", material.getId(), courseId);
            redirectAttributes.addFlashAttribute("success", 
                "Material '" + materialFile.getOriginalFilename() + "' uploaded successfully!");
            
        } catch (Exception e) {
            log.error("Material upload failed", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload material: " + e.getMessage());
        }
        
        return "redirect:/instructor/courses/" + courseId + "/edit";
    }

    /**
     * Delete course material.
     * 
     * @param courseId the course UUID
     * @param materialId the material UUID
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect
     * @return redirect back to course edit page
     */
    @PostMapping("/courses/{courseId}/materials/{materialId}/delete")
    public String deleteMaterial(
            @PathVariable("courseId") UUID courseId,
            @PathVariable("materialId") UUID materialId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            var user = userDetailsService.getUserByEmail(userDetails.getUsername());
            materialService.deleteMaterial(materialId, user.getId());
            
            log.info("Material deleted: {}", materialId);
            redirectAttributes.addFlashAttribute("success", "Material deleted successfully!");
            
        } catch (Exception e) {
            log.error("Material deletion failed", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete material: " + e.getMessage());
        }
        
        return "redirect:/instructor/courses/" + courseId + "/edit";
    }
}

