-- =====================================================
-- ETMS Database Cleanup Script
-- Employment Training Management System
-- =====================================================
-- USE THIS SCRIPT TO CLEAN UP ALL DATA (NOT THE SCHEMA)
-- Run this before re-running 02_sample_data.sql
-- =====================================================

-- Set schema
SET search_path TO etms, public;

-- Disable triggers temporarily to avoid constraint issues
SET session_replication_role = 'replica';

-- =====================================================
-- DELETE ALL DATA (in correct order to respect FK constraints)
-- =====================================================

-- Delete data from tables with dependencies first
DELETE FROM course_analytics;
DELETE FROM notifications;
DELETE FROM material_progress;
DELETE FROM course_prerequisites;
DELETE FROM feedback;
DELETE FROM live_sessions;
DELETE FROM materials;
DELETE FROM enrollments;
DELETE FROM courses;
DELETE FROM users;

-- Re-enable triggers
SET session_replication_role = 'origin';

-- =====================================================
-- VERIFY CLEANUP
-- =====================================================

SELECT 
    'Users' as table_name, 
    COUNT(*) as remaining_records
FROM users
UNION ALL
SELECT 
    'Courses' as table_name, 
    COUNT(*) as remaining_records
FROM courses
UNION ALL
SELECT 
    'Materials' as table_name, 
    COUNT(*) as remaining_records
FROM materials
UNION ALL
SELECT 
    'Enrollments' as table_name, 
    COUNT(*) as remaining_records
FROM enrollments
UNION ALL
SELECT 
    'Feedback' as table_name, 
    COUNT(*) as remaining_records
FROM feedback
UNION ALL
SELECT 
    'Live Sessions' as table_name, 
    COUNT(*) as remaining_records
FROM live_sessions
UNION ALL
SELECT 
    'Course Prerequisites' as table_name, 
    COUNT(*) as remaining_records
FROM course_prerequisites
UNION ALL
SELECT 
    'Material Progress' as table_name, 
    COUNT(*) as remaining_records
FROM material_progress
UNION ALL
SELECT 
    'Notifications' as table_name, 
    COUNT(*) as remaining_records
FROM notifications
UNION ALL
SELECT 
    'Course Analytics' as table_name, 
    COUNT(*) as remaining_records
FROM course_analytics;

-- Success message
SELECT '✅ All data has been cleaned up successfully!' as message;
SELECT '📝 You can now run 02_sample_data.sql to repopulate the database' as next_step;

