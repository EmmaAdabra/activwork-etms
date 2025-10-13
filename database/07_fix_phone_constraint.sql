-- =====================================================
-- Fix Phone Number Constraint to Allow Empty Strings
-- ETMS - Employment Training Management System
-- =====================================================

SET search_path TO etms, public;

-- Drop the old constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_phone_format;

-- Add new constraint that allows NULL or empty string or valid UK format
ALTER TABLE users ADD CONSTRAINT chk_phone_format 
    CHECK (
        phone_number IS NULL OR 
        phone_number = '' OR 
        phone_number ~* '^(\+44|0)[0-9]{10,11}$'
    );

-- Verify the change
SELECT 'Phone constraint updated successfully!' AS status;

