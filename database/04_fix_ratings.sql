-- =====================================================
-- ETMS Fix Course Ratings Script
-- Employment Training Management System
-- =====================================================
-- This script manually recalculates course ratings
-- Run this if ratings are showing as 0.00
-- =====================================================

-- Set schema
SET search_path TO etms, public;

-- Update course ratings based on feedback
UPDATE courses c
SET 
    average_rating = COALESCE((
        SELECT AVG(f.rating)::DECIMAL(3,2)
        FROM feedback f
        WHERE f.course_id = c.id
        AND f.is_visible = TRUE
    ), 0.00),
    total_ratings = COALESCE((
        SELECT COUNT(*)
        FROM feedback f
        WHERE f.course_id = c.id
        AND f.is_visible = TRUE
    ), 0);

-- Verify the update
SELECT 
    c.title as course_name,
    c.average_rating,
    c.total_ratings,
    c.enrollment_count
FROM courses c
ORDER BY c.average_rating DESC, c.enrollment_count DESC;

-- Success message
SELECT '✅ Course ratings have been recalculated!' as message;

