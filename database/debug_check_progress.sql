-- Debug query to check enrollment progress
-- Replace the enrollment_id with your actual enrollment ID

-- 1. Check enrollment status
SELECT 
    id,
    learner_id,
    course_id,
    status,
    progress_percent,
    completed_materials,
    total_materials,
    enrolled_at,
    completed_at
FROM etms.enrollments
WHERE id = 'YOUR_ENROLLMENT_ID_HERE';  -- Replace with actual enrollment ID

-- 2. Check material progress for this enrollment
SELECT 
    mp.id,
    mp.enrollment_id,
    mp.material_id,
    m.original_filename,
    m.material_type,
    mp.is_completed,
    mp.completion_percent,
    mp.time_spent_minutes,
    mp.completed_at,
    mp.updated_at
FROM etms.material_progress mp
JOIN etms.materials m ON mp.material_id = m.id
WHERE mp.enrollment_id = 'YOUR_ENROLLMENT_ID_HERE'  -- Replace with actual enrollment ID
ORDER BY mp.updated_at DESC;

-- 3. Count total active materials for the course
SELECT 
    c.id AS course_id,
    c.title AS course_title,
    COUNT(m.id) AS total_materials,
    SUM(CASE WHEN m.is_active = true THEN 1 ELSE 0 END) AS active_materials
FROM etms.courses c
LEFT JOIN etms.materials m ON c.id = m.course_id
WHERE c.id = (SELECT course_id FROM etms.enrollments WHERE id = 'YOUR_ENROLLMENT_ID_HERE')  -- Replace
GROUP BY c.id, c.title;

-- 4. Show all materials for the course
SELECT 
    m.id,
    m.original_filename,
    m.material_type,
    m.is_active,
    m.is_required,
    m.uploaded_at
FROM etms.materials m
WHERE m.course_id = (SELECT course_id FROM etms.enrollments WHERE id = 'YOUR_ENROLLMENT_ID_HERE')  -- Replace
ORDER BY m.uploaded_at;

