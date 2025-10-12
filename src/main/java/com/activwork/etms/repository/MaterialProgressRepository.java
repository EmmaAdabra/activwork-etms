package com.activwork.etms.repository;

import com.activwork.etms.model.Enrollment;
import com.activwork.etms.model.MaterialProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for MaterialProgress entity operations.
 * 
 * Provides data access methods for granular material completion tracking.
 * Enables features like video bookmarking and detailed learning analytics.
 */
@Repository
public interface MaterialProgressRepository extends JpaRepository<MaterialProgress, UUID> {

    /**
     * Find all progress records for a specific enrollment.
     * 
     * @param enrollment the enrollment
     * @return list of material progress for the enrollment
     */
    List<MaterialProgress> findByEnrollment(Enrollment enrollment);

    /**
     * Find all progress records for a specific enrollment ID.
     * 
     * @param enrollmentId the enrollment UUID
     * @return list of material progress for the enrollment
     */
    List<MaterialProgress> findByEnrollmentId(UUID enrollmentId);

    /**
     * Find progress for a specific enrollment and material.
     * 
     * @param enrollmentId the enrollment UUID
     * @param materialId the material UUID
     * @return Optional containing the progress if found
     */
    Optional<MaterialProgress> findByEnrollmentIdAndMaterialId(UUID enrollmentId, UUID materialId);

    /**
     * Find all completed materials for an enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @param isCompleted true for completed materials
     * @return list of completed material progress records
     */
    List<MaterialProgress> findByEnrollmentIdAndIsCompleted(UUID enrollmentId, Boolean isCompleted);

    /**
     * Check if progress exists for an enrollment-material combination.
     * 
     * @param enrollmentId the enrollment UUID
     * @param materialId the material UUID
     * @return true if progress record exists
     */
    boolean existsByEnrollmentIdAndMaterialId(UUID enrollmentId, UUID materialId);

    /**
     * Count completed materials for an enrollment.
     * 
     * @param enrollmentId the enrollment UUID
     * @param isCompleted true for completed materials
     * @return number of completed materials
     */
    long countByEnrollmentIdAndIsCompleted(UUID enrollmentId, Boolean isCompleted);

    /**
     * Find all progress records for a specific material across all enrollments.
     * (Analytics: How many learners completed this material?)
     * 
     * @param materialId the material UUID
     * @return list of progress records for the material
     */
    List<MaterialProgress> findByMaterialId(UUID materialId);

    /**
     * Calculate average completion percentage for a material across all learners.
     * 
     * @param materialId the material UUID
     * @return average completion percentage
     */
    @Query("SELECT COALESCE(AVG(mp.completionPercent), 0.0) FROM MaterialProgress mp WHERE mp.material.id = :materialId")
    Double calculateAverageCompletion(@Param("materialId") UUID materialId);

    /**
     * Find materials with bookmarked position (video resume feature).
     * 
     * @param enrollmentId the enrollment UUID
     * @return list of materials with saved playback positions
     */
    @Query("SELECT mp FROM MaterialProgress mp WHERE mp.enrollment.id = :enrollmentId AND mp.lastPositionSeconds > 0 AND mp.isCompleted = false")
    List<MaterialProgress> findMaterialsInProgress(@Param("enrollmentId") UUID enrollmentId);
}

