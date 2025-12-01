-- Migration script to fix email_verification_token and password_reset_token tables
-- Run this script to remove old user_id columns and keep only compte_id

USE testnova;

-- Fix email_verification_token table
-- Drop the old user_id column since we now use compte_id
ALTER TABLE email_verification_token 
DROP COLUMN IF EXISTS user_id;

-- Fix password_reset_token table  
-- Drop the old user_id column since we now use compte_id
ALTER TABLE password_reset_token 
DROP COLUMN IF EXISTS user_id;

-- Verify the changes
DESCRIBE email_verification_token;
DESCRIBE password_reset_token;
