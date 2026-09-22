-- =============================================================================
-- Migration: V10__daily_ingredient_allocations.sql
-- Description: Bang quan ly cap phat han muc bep va kiem ke hoan kho theo ngay
-- =============================================================================

CREATE TABLE IF NOT EXISTS daily_ingredient_allocations (
    id BIGSERIAL PRIMARY KEY,
    branch_id BIGINT NOT NULL,
    ingredient_id INTEGER NOT NULL,
    allocation_date DATE NOT NULL,
    allocated_quantity DECIMAL(12, 3) NOT NULL,
    used_quantity DECIMAL(12, 3) NOT NULL DEFAULT 0.000,
    actual_remaining DECIMAL(12, 3),
    returned_quantity DECIMAL(12, 3) DEFAULT 0.000,
    loss_quantity DECIMAL(12, 3) DEFAULT 0.000,
    loss_reason VARCHAR(255),
    source VARCHAR(50) NOT NULL DEFAULT 'LOCAL_STOCK',
    central_batch_no VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    note VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_daily_alloc UNIQUE (branch_id, ingredient_id, allocation_date),
    CONSTRAINT fk_alloc_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_alloc_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_alloc_branch_date ON daily_ingredient_allocations (branch_id, allocation_date);
