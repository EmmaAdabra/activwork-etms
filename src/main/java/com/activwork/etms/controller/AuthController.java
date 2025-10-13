package com.activwork.etms.controller;

import com.activwork.etms.dto.UserRegistrationDto;
import com.activwork.etms.dto.UserResponseDto;
import com.activwork.etms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for authentication operations.
 * 
 * GRASP Pattern: Controller
 * - Routes authentication requests (login, logout, register)
 * - Delegates to UserService for business logic
 * - Manages view rendering for auth pages
 * 
 * MVC Pattern:
 * - Controller: This class (handles HTTP requests)
 * - Model: DTOs and service layer
 * - View: Thymeleaf templates (login.html, register.html)
 * 
 * Endpoints:
 * - GET  /login - Display login page
 * - POST /login - Handled by Spring Security
 * - GET  /register - Display registration form
 * - POST /register - Process registration
 * - GET  /logout - Handled by Spring Security
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Display login page.
     * 
     * @param error login error parameter
     * @param logout logout success parameter
     * @param model the model for view
     * @return login view name
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        return "auth/login";
    }

    /**
     * Display registration page.
     * 
     * @param model the model for view
     * @return registration view name
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "auth/register";
    }

    /**
     * Process user registration.
     * 
     * @param registrationDto the registration data
     * @param bindingResult validation results
     * @param redirectAttributes attributes for redirect
     * @return redirect to login or back to registration
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        log.info("Registration attempt for email: {}", registrationDto.getEmail());
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        try {
            // Check if email already exists
            if (userService.existsByEmail(registrationDto.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "Email already registered");
                return "auth/register";
            }
            
            // Register user
            UserResponseDto user = userService.registerUser(registrationDto);
            
            log.info("User registered successfully: {}", user.getId());
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please login with your credentials.");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            log.error("Registration failed", e);
            
            // Handle specific validation errors
            if (e.getCause() instanceof jakarta.validation.ConstraintViolationException) {
                jakarta.validation.ConstraintViolationException cve = (jakarta.validation.ConstraintViolationException) e.getCause();
                for (var violation : cve.getConstraintViolations()) {
                    String field = violation.getPropertyPath().toString();
                    String message = violation.getMessage();
                    
                    if (field.equals("phoneNumber")) {
                        bindingResult.rejectValue("phoneNumber", "error.phoneNumber", message);
                    } else {
                        bindingResult.rejectValue("email", "error.registration", message);
                    }
                }
            } else {
                // Generic error message for users - add to global errors, not email field
                bindingResult.reject("error.registration", 
                    "Registration failed. Please check your information and try again.");
            }
            
            return "auth/register";
        }
    }
}

