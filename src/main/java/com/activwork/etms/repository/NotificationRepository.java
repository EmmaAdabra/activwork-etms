package com.activwork.etms.repository;

import com.activwork.etms.model.Notification;
import com.activwork.etms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Notification entity operations.
 * 
 * Provides data access methods for user notifications and alerts.
 * Enables real-time user engagement and communication.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Find all notifications for a specific user.
     * 
     * @param user the user
     * @return list of notifications for the user
     */
    List<Notification> findByUser(User user);

    /**
     * Find all notifications for a specific user ID.
     * 
     * @param userId the user UUID
     * @return list of notifications for the user
     */
    List<Notification> findByUserId(UUID userId);

    /**
     * Find all notifications for a user ordered by creation date (newest first).
     * 
     * @param userId the user UUID
     * @return list of notifications sorted by creation date descending
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find all unread notifications for a user.
     * 
     * @param userId the user UUID
     * @param isRead false for unread notifications
     * @return list of unread notifications
     */
    List<Notification> findByUserIdAndIsRead(UUID userId, Boolean isRead);

    /**
     * Find all unread notifications for a user ordered by date (newest first).
     * 
     * @param userId the user UUID
     * @param isRead false for unread notifications
     * @return list of unread notifications sorted by date
     */
    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(UUID userId, Boolean isRead);

    /**
     * Find all notifications by type.
     * 
     * @param type the notification type (e.g., "COURSE_ENROLLMENT", "SESSION_REMINDER")
     * @return list of notifications of the specified type
     */
    List<Notification> findByType(String type);

    /**
     * Find all notifications for a user with a specific type.
     * 
     * @param userId the user UUID
     * @param type the notification type
     * @return list of matching notifications
     */
    List<Notification> findByUserIdAndType(UUID userId, String type);

    /**
     * Count unread notifications for a user.
     * 
     * @param userId the user UUID
     * @param isRead false for unread notifications
     * @return number of unread notifications
     */
    long countByUserIdAndIsRead(UUID userId, Boolean isRead);

    /**
     * Find notifications created within a specific time period.
     * 
     * @param userId the user UUID
     * @param startDate the start of the period
     * @param endDate the end of the period
     * @return list of notifications created in the period
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate
    );

    /**
     * Delete old read notifications for a user (cleanup operation).
     * 
     * @param userId the user UUID
     * @param isRead true for read notifications
     * @param beforeDate delete notifications older than this date
     * @return number of notifications deleted
     */
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.isRead = :isRead AND n.createdAt < :beforeDate")
    int deleteOldReadNotifications(
            @Param("userId") UUID userId,
            @Param("isRead") Boolean isRead,
            @Param("beforeDate") ZonedDateTime beforeDate
    );
}

