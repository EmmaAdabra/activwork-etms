package com.activwork.etms.security;

import com.activwork.etms.model.User;
import com.activwork.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * 
 * Loads user details from the database for authentication.
 * Converts ETMS User entity to Spring Security UserDetails.
 * 
 * Architecture:
 * - Integrates ETMS user model with Spring Security
 * - Uses UserRepository for data access
 * - Maps UserRole to Spring Security authorities
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by email (username) for authentication.
     * 
     * @param email the user's email address
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check if user is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException("User account is inactive: " + email);
        }

        // Convert to Spring Security UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(
                    new SimpleGrantedAuthority(user.getRole().name())
                ))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }

    /**
     * Get ETMS User entity by email.
     * Useful for controllers that need user information after authentication.
     * 
     * @param email the user's email
     * @return ETMS User entity
     * @throws UsernameNotFoundException if user not found
     */
    public User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

