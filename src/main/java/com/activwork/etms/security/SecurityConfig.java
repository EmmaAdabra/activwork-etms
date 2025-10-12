package com.activwork.etms.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the ETMS application.
 * 
 * Implements session-based authentication (prototype approach).
 * Future: Can migrate to JWT for stateless authentication.
 * 
 * Security Features:
 * - Session-based authentication
 * - BCrypt password encoding
 * - Role-based authorization (INSTRUCTOR, LEARNER)
 * - CSRF protection enabled
 * - HTTP session management (30-minute timeout configured in application.properties)
 * 
 * Access Control:
 * - Public: /, /login, /register, /courses (browse)
 * - Instructor: /instructor/** endpoints
 * - Learner: /learner/** endpoints
 * - Authenticated: /dashboard, /profile
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Configure HTTP security and authorization rules.
     * 
     * @param http the HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/", "/login", "/register", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/courses", "/courses/search").permitAll() // Browse courses
                .requestMatchers("/courses/{id}").permitAll() // View course details
                
                // Instructor-only endpoints
                .requestMatchers("/instructor/**").hasAuthority("INSTRUCTOR")
                
                // Learner-only endpoints
                .requestMatchers("/learner/**").hasAuthority("LEARNER")
                
                // Authenticated user endpoints
                .requestMatchers("/dashboard", "/profile").authenticated()
                .requestMatchers("/enrollments/**", "/feedback/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // Only one session per user
                .maxSessionsPreventsLogin(false) // New login invalidates old session
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }

    /**
     * Password encoder bean for BCrypt hashing.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

