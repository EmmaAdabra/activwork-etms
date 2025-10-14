-- =====================================================
-- Add suspicious_activity column to material_progress table
-- ETMS - Employment Training Management System
-- =====================================================

SET search_path TO etms, public;

-- Add suspicious_activity column to material_progress table
ALTER TABLE material_progress 
ADD COLUMN suspicious_activity BOOLEAN DEFAULT FALSE;

-- Add comment for documentation
COMMENT ON COLUMN material_progress.suspicious_activity IS 'Flag for tracking suspicious learner activity (excessive seeking, etc.)';

-- Verify the change
SELECT 
    column_name, 
    data_type, 
    column_default, 
    is_nullable
FROM information_schema.columns 
WHERE table_schema = 'etms' 
  AND table_name = 'material_progress' 
  AND column_name = 'suspicious_activity';

SELECT 'Suspicious activity column added successfully!' AS status;
