package com.activwork.etms.repository;

import com.activwork.etms.model.Course;
import com.activwork.etms.model.LiveSession;
import com.activwork.etms.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for LiveSession entity operations.
 * 
 * Provides data access methods for live training sessions.
 */
@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, UUID> {

    /**
     * Find all sessions for a specific course.
     * 
     * @param course the course
     * @return list of sessions for the course
     */
    List<LiveSession> findByCourse(Course course);

    /**
     * Find all sessions for a specific course ID.
     * 
     * @param courseId the course UUID
     * @return list of sessions for the course
     */
    List<LiveSession> findByCourseId(UUID courseId);

    /**
     * Find all sessions by status.
     * 
     * @param status the session status (SCHEDULED, LIVE, COMPLETED, CANCELLED)
     * @return list of sessions with the specified status
     */
    List<LiveSession> findByStatus(SessionStatus status);

    /**
     * Find all active sessions for a course.
     * 
     * @param courseId the course UUID
     * @param isActive true for active sessions
     * @return list of active sessions
     */
    List<LiveSession> findByCourseIdAndIsActive(UUID courseId, Boolean isActive);

    /**
     * Find all sessions for a course with specific status.
     * 
     * @param courseId the course UUID
     * @param status the session status
     * @return list of matching sessions
     */
    List<LiveSession> findByCourseIdAndStatus(UUID courseId, SessionStatus status);

    /**
     * Find all upcoming sessions (scheduled and starts in the future).
     * 
     * @param now the current timestamp
     * @return list of upcoming sessions
     */
    @Query("SELECT s FROM LiveSession s WHERE s.status = 'SCHEDULED' AND s.startsAt > :now ORDER BY s.startsAt ASC")
    List<LiveSession> findUpcomingSessions(@Param("now") ZonedDateTime now);

    /**
     * Find upcoming sessions for a specific course.
     * 
     * @param courseId the course UUID
     * @param now the current timestamp
     * @return list of upcoming sessions for the course
     */
    @Query("SELECT s FROM LiveSession s WHERE s.course.id = :courseId AND s.status = 'SCHEDULED' AND s.startsAt > :now ORDER BY s.startsAt ASC")
    List<LiveSession> findUpcomingSessionsForCourse(@Param("courseId") UUID courseId, @Param("now") ZonedDateTime now);

    /**
     * Find all sessions that should be started (status = SCHEDULED and start time has passed).
     * 
     * @param now the current timestamp
     * @return list of sessions that should be in LIVE status
     */
    @Query("SELECT s FROM LiveSession s WHERE s.status = 'SCHEDULED' AND s.startsAt <= :now AND s.endsAt > :now")
    List<LiveSession> findSessionsToStart(@Param("now") ZonedDateTime now);

    /**
     * Find all live sessions that should be completed (status = LIVE and end time has passed).
     * 
     * @param now the current timestamp
     * @return list of sessions that should be completed
     */
    @Query("SELECT s FROM LiveSession s WHERE s.status = 'LIVE' AND s.endsAt <= :now")
    List<LiveSession> findSessionsToComplete(@Param("now") ZonedDateTime now);

    /**
     * Find all sessions with recordings available.
     * 
     * @param recordingAvailable true for sessions with recordings
     * @return list of sessions with recordings
     */
    List<LiveSession> findByRecordingAvailable(Boolean recordingAvailable);
}

