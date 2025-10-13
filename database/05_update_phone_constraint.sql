-- =====================================================
-- Database Migration: Update Phone Number Constraint
-- ETMS - Employment Training Management System
-- =====================================================

-- This script updates the phone number constraint to support UK format
-- Run this after the initial database setup

SET search_path TO etms, public;

-- Drop the old constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_phone_format;

-- Add the new UK-friendly constraint
ALTER TABLE users ADD CONSTRAINT chk_phone_format 
    CHECK (phone_number IS NULL OR phone_number ~* '^(\+44|0)[0-9]{10,11}$');

-- Verify the constraint was updated
SELECT conname, consrc 
FROM pg_constraint 
WHERE conname = 'chk_phone_format' 
AND conrelid = 'users'::regclass;

COMMENT ON CONSTRAINT chk_phone_format ON users IS 'UK phone number format: +447123456789 or 07123456789';

