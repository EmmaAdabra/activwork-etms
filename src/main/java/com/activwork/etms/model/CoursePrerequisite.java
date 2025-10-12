package com.activwork.etms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * CoursePrerequisite entity representing prerequisite relationships between courses.
 * Maps to the 'course_prerequisites' table in the 'etms' schema.
 * 
 * GRASP Pattern: Information Expert
 * - Responsible for course dependency information
 * - Enables structured learning paths
 */
@Entity
@Table(
    name = "course_prerequisites", 
    schema = "etms",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_prerequisite", columnNames = {"course_id", "prerequisite_course_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Prerequisite course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_course_id", nullable = false)
    private Course prerequisiteCourse;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Set creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
    }

    /**
     * Check if prerequisite is mandatory
     * @return true if prerequisite must be completed before enrolling in the course
     */
    public boolean isMandatory() {
        return Boolean.TRUE.equals(this.isMandatory);
    }

    /**
     * Check if prerequisite is optional (recommended)
     * @return true if prerequisite is recommended but not required
     */
    public boolean isOptional() {
        return !isMandatory();
    }
}

