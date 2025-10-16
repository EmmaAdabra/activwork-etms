-- =====================================================
-- ETMS Database Migration: Course Sections
-- Employment Training Management System
-- =====================================================
-- Purpose: Add course sections to organize materials into logical groups
-- Author: ETMS Development Team
-- Date: 2025-10-15
-- =====================================================

-- Set search path
SET search_path TO etms, public;

-- =====================================================
-- CREATE COURSE_SECTIONS TABLE
-- =====================================================

-- Course sections table to group materials logically
CREATE TABLE IF NOT EXISTS etms.course_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    section_order INTEGER NOT NULL DEFAULT 0,
    duration_minutes INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_section_course 
        FOREIGN KEY (course_id) 
        REFERENCES etms.courses(id) 
        ON DELETE CASCADE,
    
    -- Business constraints
    CONSTRAINT chk_title_not_empty 
        CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_section_order_positive 
        CHECK (section_order >= 0),
    CONSTRAINT chk_duration_positive 
        CHECK (duration_minutes >= 0),
    CONSTRAINT chk_title_length 
        CHECK (LENGTH(title) BETWEEN 1 AND 255)
);

-- =====================================================
-- UPDATE MATERIALS TABLE
-- =====================================================

-- Add section_id column to materials table
ALTER TABLE etms.materials 
ADD COLUMN IF NOT EXISTS section_id UUID,
ADD COLUMN IF NOT EXISTS material_order INTEGER DEFAULT 0;

-- Add foreign key constraint for section_id
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_material_section'
    ) THEN
        ALTER TABLE etms.materials 
        ADD CONSTRAINT fk_material_section 
            FOREIGN KEY (section_id) 
            REFERENCES etms.course_sections(id) 
            ON DELETE SET NULL;
    END IF;
END $$;

-- Add constraint for material_order
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'chk_material_order_positive'
    ) THEN
        ALTER TABLE etms.materials 
        ADD CONSTRAINT chk_material_order_positive 
            CHECK (material_order >= 0);
    END IF;
END $$;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Index on course_id for faster section lookups
CREATE INDEX IF NOT EXISTS idx_course_sections_course_id 
ON etms.course_sections(course_id);

-- Index on section_order for faster ordering
CREATE INDEX IF NOT EXISTS idx_course_sections_order 
ON etms.course_sections(course_id, section_order);

-- Index on is_active for filtering
CREATE INDEX IF NOT EXISTS idx_course_sections_active 
ON etms.course_sections(course_id, is_active);

-- Index on section_id for materials
CREATE INDEX IF NOT EXISTS idx_materials_section_id 
ON etms.materials(section_id);

-- Index on material_order for materials
CREATE INDEX IF NOT EXISTS idx_materials_order 
ON etms.materials(section_id, material_order);

-- Composite index for section materials ordering
CREATE INDEX IF NOT EXISTS idx_materials_section_order 
ON etms.materials(section_id, material_order, is_active);

-- =====================================================
-- TRIGGER FUNCTIONS
-- =====================================================

-- Function to update section's updated_at timestamp
CREATE OR REPLACE FUNCTION etms.update_section_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update timestamp on section update
DROP TRIGGER IF EXISTS trigger_update_section_timestamp ON etms.course_sections;
CREATE TRIGGER trigger_update_section_timestamp
    BEFORE UPDATE ON etms.course_sections
    FOR EACH ROW
    EXECUTE FUNCTION etms.update_section_timestamp();

-- Function to update course's updated_at when section changes
CREATE OR REPLACE FUNCTION etms.update_course_on_section_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE etms.courses 
    SET updated_at = CURRENT_TIMESTAMP 
    WHERE id = COALESCE(NEW.course_id, OLD.course_id);
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Trigger to update course when section is modified
DROP TRIGGER IF EXISTS trigger_course_section_change ON etms.course_sections;
CREATE TRIGGER trigger_course_section_change
    AFTER INSERT OR UPDATE OR DELETE ON etms.course_sections
    FOR EACH ROW
    EXECUTE FUNCTION etms.update_course_on_section_change();

-- Function to calculate section duration based on materials
CREATE OR REPLACE FUNCTION etms.calculate_section_duration(section_uuid UUID)
RETURNS INTEGER AS $$
DECLARE
    total_duration INTEGER;
BEGIN
    SELECT COALESCE(SUM(CEIL(duration_seconds::DECIMAL / 60)), 0)
    INTO total_duration
    FROM etms.materials
    WHERE section_id = section_uuid 
    AND is_active = TRUE;
    
    RETURN total_duration;
END;
$$ LANGUAGE plpgsql;

-- Function to update section duration when materials change
CREATE OR REPLACE FUNCTION etms.update_section_duration_on_material_change()
RETURNS TRIGGER AS $$
DECLARE
    affected_section_id UUID;
BEGIN
    -- Determine which section to update
    IF TG_OP = 'DELETE' THEN
        affected_section_id := OLD.section_id;
    ELSIF TG_OP = 'UPDATE' THEN
        -- Update both old and new section if section_id changed
        IF OLD.section_id IS DISTINCT FROM NEW.section_id THEN
            IF OLD.section_id IS NOT NULL THEN
                UPDATE etms.course_sections 
                SET duration_minutes = etms.calculate_section_duration(OLD.section_id)
                WHERE id = OLD.section_id;
            END IF;
            affected_section_id := NEW.section_id;
        ELSE
            affected_section_id := NEW.section_id;
        END IF;
    ELSE -- INSERT
        affected_section_id := NEW.section_id;
    END IF;
    
    -- Update the affected section's duration
    IF affected_section_id IS NOT NULL THEN
        UPDATE etms.course_sections 
        SET duration_minutes = etms.calculate_section_duration(affected_section_id)
        WHERE id = affected_section_id;
    END IF;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Trigger to update section duration when materials change
DROP TRIGGER IF EXISTS trigger_update_section_duration ON etms.materials;
CREATE TRIGGER trigger_update_section_duration
    AFTER INSERT OR UPDATE OR DELETE ON etms.materials
    FOR EACH ROW
    EXECUTE FUNCTION etms.update_section_duration_on_material_change();

-- =====================================================
-- HELPER FUNCTIONS
-- =====================================================

-- Function to get section completion count for an enrollment
CREATE OR REPLACE FUNCTION etms.get_section_completion_count(
    p_section_id UUID,
    p_enrollment_id UUID
)
RETURNS TABLE (
    total_materials BIGINT,
    completed_materials BIGINT,
    completion_percent NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(m.id) AS total_materials,
        COUNT(CASE WHEN mp.is_completed = TRUE THEN 1 END) AS completed_materials,
        CASE 
            WHEN COUNT(m.id) = 0 THEN 0
            ELSE ROUND((COUNT(CASE WHEN mp.is_completed = TRUE THEN 1 END)::DECIMAL / COUNT(m.id)::DECIMAL) * 100, 2)
        END AS completion_percent
    FROM etms.materials m
    LEFT JOIN etms.material_progress mp 
        ON mp.material_id = m.id 
        AND mp.enrollment_id = p_enrollment_id
    WHERE m.section_id = p_section_id 
    AND m.is_active = TRUE;
END;
$$ LANGUAGE plpgsql;

-- Function to get all sections with their progress for a course enrollment
CREATE OR REPLACE FUNCTION etms.get_course_sections_with_progress(
    p_course_id UUID,
    p_enrollment_id UUID DEFAULT NULL
)
RETURNS TABLE (
    section_id UUID,
    section_title VARCHAR,
    section_description TEXT,
    section_order INTEGER,
    section_duration_minutes INTEGER,
    total_materials BIGINT,
    completed_materials BIGINT,
    completion_percent NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        cs.id AS section_id,
        cs.title AS section_title,
        cs.description AS section_description,
        cs.section_order,
        cs.duration_minutes AS section_duration_minutes,
        COUNT(m.id) AS total_materials,
        CASE 
            WHEN p_enrollment_id IS NULL THEN 0
            ELSE COUNT(CASE WHEN mp.is_completed = TRUE THEN 1 END)
        END AS completed_materials,
        CASE 
            WHEN COUNT(m.id) = 0 THEN 0
            WHEN p_enrollment_id IS NULL THEN 0
            ELSE ROUND((COUNT(CASE WHEN mp.is_completed = TRUE THEN 1 END)::DECIMAL / COUNT(m.id)::DECIMAL) * 100, 2)
        END AS completion_percent
    FROM etms.course_sections cs
    LEFT JOIN etms.materials m 
        ON m.section_id = cs.id 
        AND m.is_active = TRUE
    LEFT JOIN etms.material_progress mp 
        ON mp.material_id = m.id 
        AND mp.enrollment_id = p_enrollment_id
    WHERE cs.course_id = p_course_id 
    AND cs.is_active = TRUE
    GROUP BY cs.id, cs.title, cs.description, cs.section_order, cs.duration_minutes
    ORDER BY cs.section_order ASC;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================

COMMENT ON TABLE etms.course_sections IS 'Course sections to organize materials into logical groups (like chapters)';
COMMENT ON COLUMN etms.course_sections.section_order IS 'Display order of sections within a course (0-based)';
COMMENT ON COLUMN etms.course_sections.duration_minutes IS 'Total duration of all materials in this section (auto-calculated)';

COMMENT ON COLUMN etms.materials.section_id IS 'Reference to the section this material belongs to (nullable for backward compatibility)';
COMMENT ON COLUMN etms.materials.material_order IS 'Display order of material within a section (0-based)';

COMMENT ON FUNCTION etms.calculate_section_duration(UUID) IS 'Calculate total duration of all active materials in a section';
COMMENT ON FUNCTION etms.get_section_completion_count(UUID, UUID) IS 'Get completion statistics for a section and enrollment';
COMMENT ON FUNCTION etms.get_course_sections_with_progress(UUID, UUID) IS 'Get all sections of a course with optional progress tracking for an enrollment';

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify table creation
SELECT 
    'course_sections table created' AS status,
    COUNT(*) AS row_count
FROM etms.course_sections;

-- Verify materials table update
SELECT 
    'materials table updated' AS status,
    COUNT(*) AS materials_count,
    COUNT(section_id) AS materials_with_section
FROM etms.materials;

-- Verify indexes
SELECT 
    schemaname,
    tablename,
    indexname
FROM pg_indexes
WHERE schemaname = 'etms' 
AND tablename IN ('course_sections', 'materials')
ORDER BY tablename, indexname;

-- =====================================================
-- MIGRATION COMPLETE
-- =====================================================

-- Success message
DO $$
BEGIN
    RAISE NOTICE '✅ Migration 10: Course Sections - COMPLETED SUCCESSFULLY';
    RAISE NOTICE '📊 Tables updated: course_sections (created), materials (updated)';
    RAISE NOTICE '🔧 Indexes created: 6 indexes for performance optimization';
    RAISE NOTICE '⚡ Triggers created: 3 triggers for automatic updates';
    RAISE NOTICE '🎯 Functions created: 4 helper functions for section management';
    RAISE NOTICE '';
    RAISE NOTICE '📝 Next steps:';
    RAISE NOTICE '   1. Update Java entities (CourseSection.java, Material.java)';
    RAISE NOTICE '   2. Create repositories and services';
    RAISE NOTICE '   3. Update course creation UI';
    RAISE NOTICE '   4. Implement accordion view for learners';
END $$;

