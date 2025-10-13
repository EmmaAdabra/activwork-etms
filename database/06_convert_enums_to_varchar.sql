-- =====================================================
-- Database Migration: Convert PostgreSQL ENUMs to VARCHAR
-- ETMS - Employment Training Management System
-- =====================================================

-- This script converts PostgreSQL custom ENUM types to VARCHAR
-- to be compatible with Hibernate's @Enumerated(EnumType.STRING)

SET search_path TO etms, public;

-- Step 1: Drop all CHECK constraints that reference ENUM values
ALTER TABLE enrollments DROP CONSTRAINT IF EXISTS chk_completion_date;
ALTER TABLE enrollments DROP CONSTRAINT IF EXISTS enrollments_status_check;

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE courses DROP CONSTRAINT IF EXISTS courses_category_check;
ALTER TABLE courses DROP CONSTRAINT IF EXISTS courses_difficulty_level_check;
ALTER TABLE courses DROP CONSTRAINT IF EXISTS courses_status_check;
ALTER TABLE materials DROP CONSTRAINT IF EXISTS materials_material_type_check;
ALTER TABLE live_sessions DROP CONSTRAINT IF EXISTS live_sessions_status_check;

-- Step 2: Alter DEFAULT values that use ENUM literals
ALTER TABLE enrollments ALTER COLUMN status DROP DEFAULT;
ALTER TABLE courses ALTER COLUMN status DROP DEFAULT;
ALTER TABLE live_sessions ALTER COLUMN status DROP DEFAULT;

-- Step 3: Convert ENUM columns to VARCHAR with explicit casting
-- Users table
ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(50) USING role::text;

-- Courses table
ALTER TABLE courses ALTER COLUMN category TYPE VARCHAR(50) USING category::text;
ALTER TABLE courses ALTER COLUMN difficulty_level TYPE VARCHAR(50) USING difficulty_level::text;
ALTER TABLE courses ALTER COLUMN status TYPE VARCHAR(50) USING status::text;

-- Materials table
ALTER TABLE materials ALTER COLUMN material_type TYPE VARCHAR(50) USING material_type::text;

-- Enrollments table
ALTER TABLE enrollments ALTER COLUMN status TYPE VARCHAR(50) USING status::text;

-- Live Sessions table
ALTER TABLE live_sessions ALTER COLUMN status TYPE VARCHAR(50) USING status::text;

-- Step 4: Restore DEFAULT values as VARCHAR literals
ALTER TABLE enrollments ALTER COLUMN status SET DEFAULT 'ACTIVE';
ALTER TABLE courses ALTER COLUMN status SET DEFAULT 'DRAFT';
ALTER TABLE live_sessions ALTER COLUMN status SET DEFAULT 'SCHEDULED';

-- Step 5: Add CHECK constraints with VARCHAR values
ALTER TABLE users ADD CONSTRAINT users_role_check 
    CHECK (role IN ('INSTRUCTOR', 'LEARNER'));

ALTER TABLE courses ADD CONSTRAINT courses_category_check 
    CHECK (category IN ('PROGRAMMING', 'WEB_DEVELOPMENT', 'DATABASE', 'DEVOPS', 
                        'CYBERSECURITY', 'DATA_SCIENCE', 'MOBILE_DEVELOPMENT', 
                        'CLOUD_COMPUTING', 'SOFTWARE_TESTING', 'PROJECT_MANAGEMENT'));

ALTER TABLE courses ADD CONSTRAINT courses_difficulty_level_check 
    CHECK (difficulty_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'));

ALTER TABLE courses ADD CONSTRAINT courses_status_check 
    CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'SUSPENDED'));

ALTER TABLE materials ADD CONSTRAINT materials_material_type_check 
    CHECK (material_type IN ('VIDEO', 'PDF', 'DOCUMENT', 'PRESENTATION', 
                             'AUDIO', 'IMAGE', 'CODE_SAMPLE', 'EXERCISE', 
                             'QUIZ', 'CERTIFICATE'));

ALTER TABLE enrollments ADD CONSTRAINT enrollments_status_check 
    CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED', 'SUSPENDED'));

ALTER TABLE live_sessions ADD CONSTRAINT live_sessions_status_check 
    CHECK (status IN ('SCHEDULED', 'LIVE', 'COMPLETED', 'CANCELLED'));

-- Step 6: Restore the completion_date constraint with VARCHAR comparison
ALTER TABLE enrollments ADD CONSTRAINT chk_completion_date CHECK (
    completion_date IS NULL OR 
    (status = 'COMPLETED' AND completion_date IS NOT NULL)
);

-- Step 7: Verify changes
SELECT 
    table_name, 
    column_name, 
    data_type, 
    character_maximum_length,
    column_default
FROM information_schema.columns
WHERE table_schema = 'etms' 
  AND column_name IN ('role', 'category', 'difficulty_level', 'status', 'material_type')
ORDER BY table_name, column_name;

-- Step 8: Add comments
COMMENT ON COLUMN users.role IS 'User role: INSTRUCTOR or LEARNER (VARCHAR with CHECK constraint)';
COMMENT ON COLUMN courses.category IS 'Course category (VARCHAR with CHECK constraint)';
COMMENT ON COLUMN courses.difficulty_level IS 'Course difficulty level (VARCHAR with CHECK constraint)';
COMMENT ON COLUMN courses.status IS 'Course status (VARCHAR with CHECK constraint)';
COMMENT ON COLUMN materials.material_type IS 'Material type (VARCHAR with CHECK constraint)';
COMMENT ON COLUMN enrollments.status IS 'Enrollment status (VARCHAR with CHECK constraint)';
COMMENT ON COLUMN live_sessions.status IS 'Session status (VARCHAR with CHECK constraint)';

-- Step 9: Note about old ENUM types
-- The old ENUM types are now unused but not dropped to prevent data loss
-- You can drop them manually later if needed:
-- DROP TYPE IF EXISTS user_role CASCADE;
-- DROP TYPE IF EXISTS course_category CASCADE;
-- DROP TYPE IF EXISTS difficulty_level CASCADE;
-- DROP TYPE IF EXISTS course_status CASCADE;
-- DROP TYPE IF EXISTS material_type CASCADE;
-- DROP TYPE IF EXISTS enrollment_status CASCADE;
-- DROP TYPE IF EXISTS session_status CASCADE;

SELECT 'Migration completed successfully!' AS status;
