package com.activwork.etms.repository;

import com.activwork.etms.model.MaterialProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for MaterialProgress entity operations.
 * 
 * Provides data access methods for tracking learner progress on materials.
 */
@Repository
public interface MaterialProgressRepository extends JpaRepository<MaterialProgress, UUID> {

    /**
     * Find material progress by enrollment and material.
     * 
     * @param enrollmentId the enrollment UUID
     * @param materialId the material UUID
     * @return Optional containing the progress if found
     */
    Optional<MaterialProgress> findByEnrollmentIdAndMaterialId(UUID enrollmentId, UUID materialId);

    /**
     * Find all material progress for an enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @return list of material progress records
     */
    List<MaterialProgress> findByEnrollmentId(UUID enrollmentId);

    /**
     * Find all completed material progress for an enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @param isCompleted true for completed materials
     * @return list of completed material progress records
     */
    List<MaterialProgress> findByEnrollmentIdAndIsCompleted(UUID enrollmentId, Boolean isCompleted);

    /**
     * Count completed materials for an enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @return number of completed materials
     */
    long countByEnrollmentIdAndIsCompleted(UUID enrollmentId, Boolean isCompleted);
}