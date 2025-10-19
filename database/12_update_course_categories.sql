-- =====================================================
-- Migration Script: Update Course Categories
-- Description: Updates the course_category enum type with 16 new comprehensive categories
-- Date: 2025-10-19
-- =====================================================

-- Step 1: Create a backup of the current category enum
DO $$
BEGIN
    RAISE NOTICE 'Starting course category migration...';
END $$;

-- Step 2: Drop the CHECK constraint on category column (if it exists)
DO $$
BEGIN
    RAISE NOTICE 'Dropping existing category check constraint...';
    
    -- Drop the constraint if it exists
    ALTER TABLE etms.courses DROP CONSTRAINT IF EXISTS courses_category_check;
    
    RAISE NOTICE 'Check constraint dropped successfully.';
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'No existing check constraint found or error occurred: %', SQLERRM;
END $$;

-- Step 3: Add new enum values to the existing course_category type
-- Note: PostgreSQL doesn't allow removing enum values, so we'll add new ones and migrate data

ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'SOFTWARE_DEVELOPMENT';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'DATABASE_MANAGEMENT';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'UI_UX_DESIGN';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'QUALITY_ASSURANCE';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'CLOUD_DEVOPS';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'DATA_SCIENCE_AI';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'IT_NETWORK_ADMIN';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'PROJECT_PRODUCT_MANAGEMENT';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'BUSINESS_ENTREPRENEURSHIP';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'LEADERSHIP_MANAGEMENT';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'PROFESSIONAL_SKILLS';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'PERSONAL_DEVELOPMENT_WELLNESS';
ALTER TYPE etms.course_category ADD VALUE IF NOT EXISTS 'OTHERS';

-- Step 4: Migrate existing course data to new categories
DO $$
BEGIN
    RAISE NOTICE 'Migrating existing courses to new categories...';
    
    -- Migrate WEB_DEVELOPMENT -> SOFTWARE_DEVELOPMENT
    UPDATE etms.courses 
    SET category = 'SOFTWARE_DEVELOPMENT' 
    WHERE category = 'WEB_DEVELOPMENT';
    
    -- Migrate DATABASE -> DATABASE_MANAGEMENT
    UPDATE etms.courses 
    SET category = 'DATABASE_MANAGEMENT' 
    WHERE category = 'DATABASE';
    
    -- Migrate SOFTWARE_TESTING -> QUALITY_ASSURANCE
    UPDATE etms.courses 
    SET category = 'QUALITY_ASSURANCE' 
    WHERE category = 'SOFTWARE_TESTING';
    
    -- Migrate CLOUD_COMPUTING + DEVOPS -> CLOUD_DEVOPS
    UPDATE etms.courses 
    SET category = 'CLOUD_DEVOPS' 
    WHERE category IN ('CLOUD_COMPUTING', 'DEVOPS');
    
    -- Migrate DATA_SCIENCE -> DATA_SCIENCE_AI
    UPDATE etms.courses 
    SET category = 'DATA_SCIENCE_AI' 
    WHERE category = 'DATA_SCIENCE';
    
    -- Migrate PROJECT_MANAGEMENT -> PROJECT_PRODUCT_MANAGEMENT
    UPDATE etms.courses 
    SET category = 'PROJECT_PRODUCT_MANAGEMENT' 
    WHERE category = 'PROJECT_MANAGEMENT';
    
    RAISE NOTICE 'Course migration completed successfully!';
END $$;

-- Step 5: Display migration summary
DO $$
DECLARE
    category_count RECORD;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Migration Summary - Courses per Category';
    RAISE NOTICE '========================================';
    
    FOR category_count IN 
        SELECT category, COUNT(*) as count 
        FROM etms.courses 
        GROUP BY category 
        ORDER BY category
    LOOP
        RAISE NOTICE '% : % courses', category_count.category, category_count.count;
    END LOOP;
    
    RAISE NOTICE '========================================';
END $$;

-- Step 6: Verification queries (commented out - uncomment to run manually)
-- Check all categories in use
-- SELECT DISTINCT category FROM etms.courses ORDER BY category;

-- Count courses per category
-- SELECT category, COUNT(*) as course_count 
-- FROM etms.courses 
-- GROUP BY category 
-- ORDER BY course_count DESC, category;

-- Step 7: Notes for manual cleanup (if needed)
-- The old enum values (WEB_DEVELOPMENT, DATABASE, SOFTWARE_TESTING, CLOUD_COMPUTING, 
-- DEVOPS, DATA_SCIENCE, PROJECT_MANAGEMENT) are still in the enum type but no longer used.
-- PostgreSQL doesn't support dropping enum values directly.
-- If you need a completely clean enum, you would need to:
-- 1. Create a new enum type with only the new values
-- 2. Alter the table to use the new enum type
-- 3. Drop the old enum type
-- This is optional and not required for the system to function correctly.

-- =====================================================
-- Migration Complete!
-- =====================================================
-- New Categories (16 total):
-- 1. PROGRAMMING
-- 2. SOFTWARE_DEVELOPMENT (replaces WEB_DEVELOPMENT)
-- 3. MOBILE_DEVELOPMENT
-- 4. DATABASE_MANAGEMENT (replaces DATABASE)
-- 5. UI_UX_DESIGN
-- 6. QUALITY_ASSURANCE (replaces SOFTWARE_TESTING)
-- 7. CLOUD_DEVOPS (merges CLOUD_COMPUTING + DEVOPS)
-- 8. CYBERSECURITY
-- 9. DATA_SCIENCE_AI (replaces DATA_SCIENCE)
-- 10. IT_NETWORK_ADMIN
-- 11. PROJECT_PRODUCT_MANAGEMENT (replaces PROJECT_MANAGEMENT)
-- 12. BUSINESS_ENTREPRENEURSHIP
-- 13. LEADERSHIP_MANAGEMENT
-- 14. PROFESSIONAL_SKILLS
-- 15. PERSONAL_DEVELOPMENT_WELLNESS
-- 16. OTHERS
-- =====================================================

