package com.activwork.etms.service;

import com.activwork.etms.dto.*;
import com.activwork.etms.exception.ResourceNotFoundException;
import com.activwork.etms.model.*;
import com.activwork.etms.repository.CourseRepository;
import com.activwork.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Course entity operations.
 * 
 * GRASP Pattern: Information Expert
 * - This service is the expert on course-related business logic
 * - Knows all the rules about creating, updating, publishing courses
 * - Responsible for course lifecycle management (DRAFT → PUBLISHED → ARCHIVED)
 * - Validates business rules before persisting data
 * 
 * GRASP Pattern: Creator
 * - Creates Course objects from DTOs
 * - Responsible for instantiating courses with proper initial state
 * 
 * Architecture:
 * - Uses CourseRepository and UserRepository for data access
 * - Throws domain exceptions for business rule violations
 * - Returns DTOs (never exposes entities)
 * - Transaction management with @Transactional
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Create a new course (GRASP: Creator pattern).
     * 
     * @param courseCreateDto course creation data
     * @param instructorId the instructor creating the course
     * @return created course response
     * @throws ResourceNotFoundException if instructor not found
     * @throws IllegalArgumentException if instructor is not an instructor
     */
    @Transactional
    public CourseResponseDto createCourse(CourseCreateDto courseCreateDto, UUID instructorId) {
        log.info("Creating new course: {} by instructor: {}", courseCreateDto.getTitle(), instructorId);
        
        // Get instructor
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", instructorId));
        
        // Business rule: Only instructors can create courses
        if (!instructor.isInstructor()) {
            throw new IllegalArgumentException("Only instructors can create courses");
        }
        
        // Create course entity (GRASP: Creator)
        Course course = new Course();
        course.setTitle(courseCreateDto.getTitle());
        course.setSummary(courseCreateDto.getSummary());
        course.setDescription(courseCreateDto.getDescription());
        course.setInstructor(instructor);
        course.setCategory(courseCreateDto.getCategory());
        course.setDurationHours(courseCreateDto.getDurationHours());
        course.setStatus(CourseStatus.DRAFT); // Always start as draft
        course.setThumbnailUrl(courseCreateDto.getThumbnailUrl());
        course.setVideoPreviewUrl(courseCreateDto.getVideoPreviewUrl());
        course.setPrerequisites(courseCreateDto.getPrerequisites());
        course.setLearningObjectives(courseCreateDto.getLearningObjectives());
        course.setTags(courseCreateDto.getTags());
        course.setEndDate(courseCreateDto.getEndDate());
        course.setIsActive(true);
        course.setIsFeatured(false);
        
        Course savedCourse = courseRepository.save(course);
        
        log.info("Course created successfully with ID: {}", savedCourse.getId());
        return CourseResponseDto.fromEntity(savedCourse);
    }

    /**
     * Update an existing course (GRASP: Information Expert).
     * 
     * @param courseId the course UUID
     * @param courseUpdateDto course update data
     * @param instructorId the instructor updating the course
     * @return updated course response
     * @throws ResourceNotFoundException if course not found
     * @throws IllegalArgumentException if instructor doesn't own the course
     */
    @Transactional
    public CourseResponseDto updateCourse(UUID courseId, CourseUpdateDto courseUpdateDto, UUID instructorId) {
        log.info("Updating course: {} by instructor: {}", courseId, instructorId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Business rule: Only the course instructor can update it
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can update this course");
        }
        
        // Update fields (only if provided)
        if (courseUpdateDto.getTitle() != null) {
            course.setTitle(courseUpdateDto.getTitle());
        }
        if (courseUpdateDto.getSummary() != null) {
            course.setSummary(courseUpdateDto.getSummary());
        }
        if (courseUpdateDto.getDescription() != null) {
            course.setDescription(courseUpdateDto.getDescription());
        }
        if (courseUpdateDto.getCategory() != null) {
            course.setCategory(courseUpdateDto.getCategory());
        }
        if (courseUpdateDto.getDurationHours() != null) {
            course.setDurationHours(courseUpdateDto.getDurationHours());
        }
        if (courseUpdateDto.getStatus() != null) {
            course.setStatus(courseUpdateDto.getStatus());
        }
        if (courseUpdateDto.getThumbnailUrl() != null) {
            course.setThumbnailUrl(courseUpdateDto.getThumbnailUrl());
        }
        if (courseUpdateDto.getVideoPreviewUrl() != null) {
            course.setVideoPreviewUrl(courseUpdateDto.getVideoPreviewUrl());
        }
        if (courseUpdateDto.getPrerequisites() != null) {
            course.setPrerequisites(courseUpdateDto.getPrerequisites());
        }
        if (courseUpdateDto.getLearningObjectives() != null) {
            course.setLearningObjectives(courseUpdateDto.getLearningObjectives());
        }
        if (courseUpdateDto.getTags() != null) {
            course.setTags(courseUpdateDto.getTags());
        }
        if (courseUpdateDto.getEndDate() != null) {
            course.setEndDate(courseUpdateDto.getEndDate());
        }
        if (courseUpdateDto.getIsFeatured() != null) {
            course.setIsFeatured(courseUpdateDto.getIsFeatured());
        }
        if (courseUpdateDto.getIsActive() != null) {
            course.setIsActive(courseUpdateDto.getIsActive());
        }
        
        Course updatedCourse = courseRepository.save(course);
        
        log.info("Course updated successfully: {}", courseId);
        return CourseResponseDto.fromEntity(updatedCourse);
    }

    /**
     * Publish a course (GRASP: Information Expert - knows publishing rules).
     * 
     * @param courseId the course UUID
     * @param instructorId the instructor publishing the course
     * @return published course response
     * @throws ResourceNotFoundException if course not found
     * @throws IllegalArgumentException if course cannot be published
     */
    @Transactional
    public CourseResponseDto publishCourse(UUID courseId, UUID instructorId) {
        log.info("Publishing course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Business rule: Only the course instructor can publish
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can publish this course");
        }
        
        // Business rule: Course must be in DRAFT status to publish
        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new IllegalArgumentException("Only draft courses can be published");
        }
        
        // Business rule: Course must have required information
        if (course.getTitle() == null || course.getDescription() == null) {
            throw new IllegalArgumentException("Course must have title and description to be published");
        }
        
        course.publish(); // Uses entity method
        Course publishedCourse = courseRepository.save(course);
        
        log.info("Course published successfully: {}", courseId);
        return CourseResponseDto.fromEntity(publishedCourse);
    }

    /**
     * Archive a course.
     * 
     * @param courseId the course UUID
     * @param instructorId the instructor archiving the course
     * @return archived course response
     */
    @Transactional
    public CourseResponseDto archiveCourse(UUID courseId, UUID instructorId) {
        log.info("Archiving course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can archive this course");
        }
        
        course.archive();
        Course archivedCourse = courseRepository.save(course);
        
        log.info("Course archived successfully: {}", courseId);
        return CourseResponseDto.fromEntity(archivedCourse);
    }

    /**
     * Get course by ID.
     * 
     * @param courseId the course UUID
     * @return course response
     * @throws ResourceNotFoundException if course not found
     */
    public CourseResponseDto getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Increment view count
        return CourseResponseDto.fromEntity(course);
    }

    /**
     * Get all courses by instructor.
     * 
     * @param instructorId the instructor UUID
     * @return list of instructor's courses
     */
    public List<CourseResponseDto> getCoursesByInstructor(UUID instructorId) {
        List<Course> courses = courseRepository.findByInstructorId(instructorId);
        return courses.stream()
                .map(CourseResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all published and active courses (available for enrollment).
     * 
     * @return list of available courses
     */
    public List<CourseListDto> getAvailableCourses() {
        List<Course> courses = courseRepository.findAvailableCourses();
        return courses.stream()
                .map(CourseListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Search courses by title keyword.
     * 
     * @param keyword the search keyword
     * @return list of matching courses
     */
    public List<CourseListDto> searchCoursesByTitle(String keyword) {
        List<Course> courses = courseRepository.searchByTitle(keyword);
        return courses.stream()
                .map(CourseListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get courses by category.
     * 
     * @param category the course category
     * @return list of courses in the category
     */
    public List<CourseListDto> getCoursesByCategory(CourseCategory category) {
        List<Course> courses = courseRepository.findByCategory(category);
        return courses.stream()
                .map(CourseListDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * Get featured courses.
     * 
     * @return list of featured courses
     */
    public List<CourseListDto> getFeaturedCourses() {
        List<Course> courses = courseRepository.findByIsFeatured(true);
        return courses.stream()
                .filter(course -> CourseStatus.PUBLISHED.equals(course.getStatus()))
                .map(CourseListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Delete a course.
     * 
     * @param courseId the course UUID
     * @param instructorId the instructor deleting the course
     * @throws ResourceNotFoundException if course not found
     * @throws IllegalArgumentException if instructor doesn't own the course or course has enrollments
     */
    @Transactional
    public void deleteCourse(UUID courseId, UUID instructorId) {
        log.info("Deleting course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        // Business rule: Only the course instructor can delete
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can delete this course");
        }
        
        // Business rule: Cannot delete course with active enrollments
        if (course.getEnrollmentCount() != null && course.getEnrollmentCount() > 0) {
            throw new IllegalArgumentException("Cannot delete course with existing enrollments. Archive it instead.");
        }
        
        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", courseId);
    }

    /**
     * Increment course view count.
     * Called when someone views a course.
     * 
     * @param courseId the course UUID
     */
    @Transactional
    public void incrementViewCount(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        course.incrementViewCount();
        courseRepository.save(course);
    }

    /**
     * Check if course is available for enrollment (GRASP: Information Expert).
     * 
     * @param courseId the course UUID
     * @return true if course is available
     */
    public boolean isCourseAvailableForEnrollment(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        
        return course.isAvailableForEnrollment();
    }

    /**
     * Get all courses (admin function).
     * 
     * @return list of all courses
     */
    public List<CourseListDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(CourseListDto::fromEntity)
                .collect(Collectors.toList());
    }
}

