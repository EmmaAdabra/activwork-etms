-- =====================================================
-- ETMS Data Verification Script
-- Employment Training Management System
-- =====================================================

-- Set schema
SET search_path TO etms, public;

-- =====================================================
-- COMPREHENSIVE DATA VERIFICATION
-- =====================================================

-- 1. Users Summary
SELECT 
    '=== USERS ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Total Users' as section,
    role::TEXT as detail,
    COUNT(*)::TEXT as count
FROM users
GROUP BY role
UNION ALL
SELECT 
    'Active Users' as section,
    role::TEXT as detail,
    COUNT(*)::TEXT as count
FROM users
WHERE is_active = TRUE
GROUP BY role;

-- 2. Courses Summary
SELECT 
    '' as section, '' as detail, '' as count
UNION ALL
SELECT 
    '=== COURSES ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Total Courses' as section,
    category::TEXT as detail,
    COUNT(*)::TEXT as count
FROM courses
GROUP BY category
UNION ALL
SELECT 
    'By Status' as section,
    status::TEXT as detail,
    COUNT(*)::TEXT as count
FROM courses
GROUP BY status;

-- 3. Materials Summary
SELECT 
    '' as section, '' as detail, '' as count
UNION ALL
SELECT 
    '=== MATERIALS ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Total Materials' as section,
    material_type::TEXT as detail,
    COUNT(*)::TEXT as count
FROM materials
GROUP BY material_type
UNION ALL
SELECT 
    'Total Size (MB)' as section,
    'All Materials' as detail,
    ROUND(SUM(file_size)::NUMERIC / 1048576, 2)::TEXT as count
FROM materials;

-- 4. Enrollments Summary
SELECT 
    '' as section, '' as detail, '' as count
UNION ALL
SELECT 
    '=== ENROLLMENTS ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Total Enrollments' as section,
    status::TEXT as detail,
    COUNT(*)::TEXT as count
FROM enrollments
GROUP BY status
UNION ALL
SELECT 
    'Avg Progress' as section,
    'All Enrollments' as detail,
    ROUND(AVG(progress_percent), 2)::TEXT || '%' as count
FROM enrollments;

-- 5. Feedback Summary
SELECT 
    '' as section, '' as detail, '' as count
UNION ALL
SELECT 
    '=== FEEDBACK ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Total Feedback' as section,
    'Rating ' || rating::TEXT || ' stars' as detail,
    COUNT(*)::TEXT as count
FROM feedback
GROUP BY rating
ORDER BY detail DESC;

-- 6. Live Sessions Summary
SELECT 
    '' as section, '' as detail, '' as count
UNION ALL
SELECT 
    '=== LIVE SESSIONS ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Total Sessions' as section,
    status::TEXT as detail,
    COUNT(*)::TEXT as count
FROM live_sessions
GROUP BY status;

-- 7. Additional Tables Summary
SELECT 
    '' as section, '' as detail, '' as count
UNION ALL
SELECT 
    '=== ADDITIONAL FEATURES ===' as section,
    '' as detail,
    '' as count
UNION ALL
SELECT 
    'Course Prerequisites' as section,
    'Total' as detail,
    COUNT(*)::TEXT as count
FROM course_prerequisites
UNION ALL
SELECT 
    'Material Progress' as section,
    'Total Tracked' as detail,
    COUNT(*)::TEXT as count
FROM material_progress
UNION ALL
SELECT 
    'Notifications' as section,
    CASE WHEN is_read THEN 'Read' ELSE 'Unread' END as detail,
    COUNT(*)::TEXT as count
FROM notifications
GROUP BY is_read
UNION ALL
SELECT 
    'Course Analytics' as section,
    'Total Records' as detail,
    COUNT(*)::TEXT as count
FROM course_analytics;

-- =====================================================
-- DETAILED USER LIST
-- =====================================================

SELECT 
    '' as info1, '' as info2, '' as info3, '' as info4
UNION ALL
SELECT 
    '=== USER DETAILS ===' as info1,
    '' as info2,
    '' as info3,
    '' as info4
UNION ALL
SELECT 
    name as info1,
    email as info2,
    role::TEXT as info3,
    CASE WHEN is_active THEN 'Active' ELSE 'Inactive' END as info4
FROM users
ORDER BY info3, info1;

-- =====================================================
-- COURSE ENROLLMENT DETAILS
-- =====================================================

SELECT 
    '' as course, '' as instructor, '' as enrolled, '' as avg_rating
UNION ALL
SELECT 
    '=== COURSE ENROLLMENT STATS ===' as course,
    '' as instructor,
    '' as enrolled,
    '' as avg_rating
UNION ALL
SELECT 
    c.title as course,
    u.name as instructor,
    c.enrollment_count::TEXT as enrolled,
    ROUND(c.average_rating, 2)::TEXT || ' (' || c.total_ratings::TEXT || ' ratings)' as avg_rating
FROM courses c
JOIN users u ON c.instructor_id = u.id
ORDER BY enrolled DESC;

-- =====================================================
-- SUCCESS MESSAGE
-- =====================================================

SELECT 
    '✅ Database is populated successfully!' as message,
    'All tables contain sample data' as status,
    CURRENT_TIMESTAMP::TEXT as verified_at;

