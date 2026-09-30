-- =============================================================================
-- Migration: V13__add_branch_id_to_dining_tables.sql
-- Description: Bo sung cot branch_id cho bang dining_tables de phu hop voi DiningTable entity
-- =============================================================================

ALTER TABLE dining_tables ADD COLUMN IF NOT EXISTS branch_id BIGINT;

-- Dong bo branch_id tu bang areas cho cac ban an hien tai
UPDATE dining_tables dt
SET branch_id = a.branch_id
FROM areas a
WHERE dt.area_id = a.id AND dt.branch_id IS NULL;

-- Neu con ban an nao chua co branch_id thi gan mac dinh vao chi nhanh 1
UPDATE dining_tables SET branch_id = 1 WHERE branch_id IS NULL;

-- Gan rang buoc NOT NULL sau khi da dien du lieu
ALTER TABLE dining_tables ALTER COLUMN branch_id SET NOT NULL;

-- Tao khoa ngoai va index cho branch_id tren bang dining_tables
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_dining_table_branch'
    ) THEN
        ALTER TABLE dining_tables 
        ADD CONSTRAINT fk_dining_table_branch 
        FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_dining_tables_branch_area ON dining_tables (branch_id, area_id);
