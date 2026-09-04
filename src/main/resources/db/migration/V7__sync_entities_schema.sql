-- =============================================================================
-- Migration: V7__sync_entities_schema.sql
-- Description: Bổ sung các cột còn thiếu giữa JPA Entity và Schema CSDL
-- =============================================================================

-- Bookings table
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS deposit_amount DECIMAL(12, 2) DEFAULT 0.00;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS party_size INT NOT NULL DEFAULT 1;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS special_request TEXT;

-- Branches table
ALTER TABLE branches ADD COLUMN IF NOT EXISTS code VARCHAR(50);
ALTER TABLE branches ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Menu Items table
ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS is_available BOOLEAN NOT NULL DEFAULT TRUE;

-- Ingredients table
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS avg_cost_price FLOAT DEFAULT 0.0;
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS min_stock_alert FLOAT DEFAULT 0.0;
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS unit VARCHAR(255);

-- Orders table
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(12, 2) DEFAULT 0.00;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS tax_amount DECIMAL(12, 2) DEFAULT 0.00;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS final_amount DECIMAL(12, 2) DEFAULT 0.00;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS booking_id BIGINT;
