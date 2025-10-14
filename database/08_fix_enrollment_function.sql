-- =====================================================
-- Fix Enrollment Function Schema Issue
-- ETMS - Employment Training Management System
-- =====================================================

-- This script fixes the update_course_enrollment_count() function
-- to properly reference the courses table in the etms schema

SET search_path TO etms, public;

-- Drop the existing function and trigger
DROP TRIGGER IF EXISTS trigger_update_enrollment_count ON enrollments;
DROP FUNCTION IF EXISTS update_course_enrollment_count();

-- Recreate the function with proper schema qualification
CREATE OR REPLACE FUNCTION update_course_enrollment_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE etms.courses 
        SET enrollment_count = enrollment_count + 1 
        WHERE id = NEW.course_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE etms.courses 
        SET enrollment_count = enrollment_count - 1 
        WHERE id = OLD.course_id;
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.course_id != NEW.course_id THEN
            UPDATE etms.courses 
            SET enrollment_count = enrollment_count - 1 
            WHERE id = OLD.course_id;
            UPDATE etms.courses 
            SET enrollment_count = enrollment_count + 1 
            WHERE id = NEW.course_id;
        END IF;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ language 'plpgsql';

-- Recreate the trigger
CREATE TRIGGER trigger_update_enrollment_count
    AFTER INSERT OR UPDATE OR DELETE ON etms.enrollments
    FOR EACH ROW EXECUTE FUNCTION update_course_enrollment_count();

-- Also fix the update_course_rating function to be safe
DROP TRIGGER IF EXISTS trigger_update_course_rating ON feedback;
DROP FUNCTION IF EXISTS update_course_rating();

CREATE OR REPLACE FUNCTION update_course_rating()
RETURNS TRIGGER AS $$
DECLARE
    course_avg_rating DECIMAL(3,2);
    course_total_ratings INTEGER;
BEGIN
    -- Calculate new average rating for the course
    SELECT 
        COALESCE(AVG(rating), 0.00),
        COUNT(*)
    INTO course_avg_rating, course_total_ratings
    FROM etms.feedback 
    WHERE course_id = COALESCE(NEW.course_id, OLD.course_id)
    AND is_visible = TRUE;
    
    -- Update course table
    UPDATE etms.courses 
    SET 
        average_rating = course_avg_rating,
        total_ratings = course_total_ratings
    WHERE id = COALESCE(NEW.course_id, OLD.course_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ language 'plpgsql';

-- Recreate the trigger
CREATE TRIGGER trigger_update_course_rating
    AFTER INSERT OR UPDATE OR DELETE ON etms.feedback
    FOR EACH ROW EXECUTE FUNCTION update_course_rating();

-- Also fix the update_enrollment_progress function
DROP TRIGGER IF EXISTS trigger_update_enrollment_progress ON material_progress;
DROP FUNCTION IF EXISTS update_enrollment_progress();

CREATE OR REPLACE FUNCTION update_enrollment_progress()
RETURNS TRIGGER AS $$
DECLARE
    v_total_materials INTEGER;
    v_completed_materials INTEGER;
    v_progress_percent DECIMAL(5,2);
BEGIN
    -- Get total materials for the course
    SELECT COUNT(*) INTO v_total_materials
    FROM etms.materials 
    WHERE course_id = (SELECT course_id FROM etms.enrollments WHERE id = NEW.enrollment_id)
    AND is_active = TRUE;
    
    -- Get completed materials for this enrollment
    SELECT COUNT(*) INTO v_completed_materials
    FROM etms.material_progress 
    WHERE enrollment_id = NEW.enrollment_id 
    AND is_completed = TRUE;
    
    -- Calculate progress percentage
    IF v_total_materials > 0 THEN
        v_progress_percent := (v_completed_materials::DECIMAL / v_total_materials::DECIMAL) * 100;
    ELSE
        v_progress_percent := 0;
    END IF;
    
    -- Update enrollment
    UPDATE etms.enrollments 
    SET 
        progress_percent = v_progress_percent,
        completed_materials = v_completed_materials,
        total_materials = v_total_materials,
        status = CASE 
            WHEN v_progress_percent = 100 THEN 'COMPLETED'::VARCHAR
            ELSE status
        END,
        completion_date = CASE 
            WHEN v_progress_percent = 100 AND status != 'COMPLETED' THEN CURRENT_TIMESTAMP
            ELSE completion_date
        END
    WHERE id = NEW.enrollment_id;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Recreate the trigger
CREATE TRIGGER trigger_update_enrollment_progress
    AFTER INSERT OR UPDATE ON etms.material_progress
    FOR EACH ROW EXECUTE FUNCTION update_enrollment_progress();

-- Verify the functions exist
SELECT 
    routine_name, 
    routine_type,
    routine_schema
FROM information_schema.routines 
WHERE routine_schema = 'etms' 
  AND routine_name LIKE '%enrollment%' 
  OR routine_name LIKE '%course%'
ORDER BY routine_name;

SELECT 'Enrollment function fix completed successfully!' AS status;
