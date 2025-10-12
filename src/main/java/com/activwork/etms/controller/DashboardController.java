package com.activwork.etms.controller;

import com.activwork.etms.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for user dashboards.
 * 
 * GRASP Pattern: Controller
 * - Routes dashboard requests based on user role
 * - Displays personalized content for instructors and learners
 * 
 * Endpoints:
 * - GET /dashboard - Role-based dashboard (redirects to instructor or learner)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Display role-based dashboard.
     * Redirects to appropriate dashboard based on user role.
     * 
     * @param userDetails the authenticated user
     * @param model the model for view
     * @return dashboard view name based on role
     */
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        log.info("Displaying dashboard for user: {}", userDetails.getUsername());
        
        // Get ETMS user entity
        var user = userDetailsService.getUserByEmail(userDetails.getUsername());
        
        model.addAttribute("user", user);
        
        // Redirect based on role
        if (user.isInstructor()) {
            return "redirect:/instructor/dashboard";
        } else {
            return "redirect:/learner/dashboard";
        }
    }
}

