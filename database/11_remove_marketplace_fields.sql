-- =====================================================
-- Remove Marketplace Fields from Courses Table
-- =====================================================
-- This script removes fields that were used for marketplace functionality
-- but are no longer needed for the training management system:
-- - price: Courses are now free by default
-- - max_enrollments: No enrollment limits for prerecorded content
-- - start_date: Courses start anytime, not scheduled
-- - enrollment_deadline: No time restrictions for enrollment

-- Remove unused columns from courses table
ALTER TABLE etms.courses 
DROP COLUMN IF EXISTS price,
DROP COLUMN IF EXISTS max_enrollments,
DROP COLUMN IF EXISTS start_date,
DROP COLUMN IF EXISTS enrollment_deadline,
DROP COLUMN IF EXISTS difficulty_level;

-- Update any existing courses to ensure they're available for enrollment
-- (remove the max_enrollments check from isAvailableForEnrollment logic)
UPDATE etms.courses 
SET is_active = true 
WHERE status = 'PUBLISHED' AND is_active = false;

-- Add comment to document the change
COMMENT ON TABLE etms.courses IS 'Training courses - marketplace fields removed, now focused on learning content';

-- Verify the changes
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_schema = 'etms' 
  AND table_name = 'courses' 
  AND column_name IN ('price', 'max_enrollments', 'start_date', 'enrollment_deadline', 'difficulty_level');

-- Should return no rows if columns were successfully removed

