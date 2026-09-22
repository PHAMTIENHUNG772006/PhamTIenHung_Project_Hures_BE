-- =============================================================================
-- Migration: V9__sync_branches_and_all_entities.sql
-- Description: Bổ sung các cột image, status và đồng bộ toàn bộ schema cho branches
-- =============================================================================

ALTER TABLE branches
    ADD COLUMN IF NOT EXISTS image VARCHAR(255),
    ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS code VARCHAR(50),
    ADD COLUMN IF NOT EXISTS name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS address TEXT,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(50),
    ADD COLUMN IF NOT EXISTS email VARCHAR(255),
    ADD COLUMN IF NOT EXISTS tax_code VARCHAR(50),
    ADD COLUMN IF NOT EXISTS opening_time TIME,
    ADD COLUMN IF NOT EXISTS closing_time TIME,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- Chuyển dữ liệu image_url sang image nếu có
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'branches' AND column_name = 'image_url'
    ) THEN
        UPDATE branches SET image = image_url WHERE image IS NULL AND image_url IS NOT NULL;
    END IF;
END $$;

-- Chuyển dữ liệu is_active sang status nếu có
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'branches' AND column_name = 'is_active'
    ) THEN
        UPDATE branches SET status = CASE WHEN is_active = FALSE THEN 'INACTIVE' ELSE 'ACTIVE' END WHERE status IS NULL;
    END IF;
END $$;
