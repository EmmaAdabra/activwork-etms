package com.activwork.etms.controller;

import com.activwork.etms.dto.CourseListDto;
import com.activwork.etms.dto.FeedbackResponseDto;
import com.activwork.etms.model.CourseCategory;
import com.activwork.etms.security.CustomUserDetailsService;
import com.activwork.etms.service.CourseService;
import com.activwork.etms.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for public home and course browsing pages.
 * 
 * GRASP Pattern: Controller
 * - Routes public requests (homepage, course browsing)
 * - Delegates to CourseService for data
 * - No authentication required
 * 
 * Endpoints:
 * - GET / - Homepage
 * - GET /courses - Browse all courses
 * - GET /courses/search - Search courses
 * - GET /courses/{id} - View course details
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;
    private final CustomUserDetailsService customUserDetailsService;
    private final FeedbackService feedbackService;

    /**
     * Display homepage.
     * 
     * @param userDetails optional authenticated user details
     * @param model the model for view
     * @return homepage view name
     */
    @GetMapping("/")
    public String showHomePage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        log.info("Displaying homepage");
        
        // Get featured courses for homepage
        List<CourseListDto> featuredCourses = courseService.getFeaturedCourses();
        model.addAttribute("featuredCourses", featuredCourses);
        
        // Add user info if authenticated
        if (userDetails != null) {
            var user = customUserDetailsService.getUserByEmail(userDetails.getUsername());
            model.addAttribute("user", user);
        }
        
        return "home";
    }

    /**
     * Browse all available courses.
     * 
     * @param category optional category filter
     * @param model the model for view
     * @return courses browse view name
     */
    @GetMapping("/courses")
    public String browseCourses(
            @RequestParam(required = false) CourseCategory category,
            Model model) {
        
        log.info("Browsing courses - category: {}", category);
        
        List<CourseListDto> courses;
        
        if (category != null) {
            courses = courseService.getCoursesByCategory(category);
        } else {
            courses = courseService.getAvailableCourses();
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("categories", CourseCategory.values());
        model.addAttribute("selectedCategory", category);
        
        return "courses/browse";
    }

    /**
     * Search courses by keyword.
     * 
     * @param keyword the search keyword
     * @param model the model for view
     * @return courses browse view name
     */
    @GetMapping("/courses/search")
    public String searchCourses(
            @RequestParam String keyword,
            Model model) {
        
        log.info("Searching courses with keyword: {}", keyword);
        
        List<CourseListDto> courses = courseService.searchCoursesByTitle(keyword);
        
        model.addAttribute("courses", courses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", CourseCategory.values());
        
        return "courses/browse";
    }

    /**
     * View course details.
     * 
     * @param id the course UUID
     * @param model the model for view
     * @return course details view name
     */
    @GetMapping("/courses/{id}")
    public String viewCourse(@PathVariable UUID id, Model model) {
        log.info("Viewing course details: {}", id);
        
        var course = courseService.getCourseById(id);
        
        // Increment view count
        courseService.incrementViewCount(id);
        
        // Load course feedback (first 5 visible feedback)
        List<FeedbackResponseDto> feedback = feedbackService.getVisibleFeedbackByCourse(id);
        List<FeedbackResponseDto> displayedFeedback = feedback.stream()
                .limit(5)
                .collect(Collectors.toList());
        
        model.addAttribute("course", course);
        model.addAttribute("feedback", displayedFeedback);
        model.addAttribute("totalFeedbackCount", feedback.size());
        model.addAttribute("hasMoreFeedback", feedback.size() > 5);
        
        return "courses/details";
    }
}

