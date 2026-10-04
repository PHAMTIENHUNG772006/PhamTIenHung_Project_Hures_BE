-- V14__add_version_for_optimistic_locking.sql
-- Thêm cột version phục vụ Optimistic Locking chống race condition cho tồn kho và cấp phát hàng ngày

ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE daily_ingredient_allocations ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;
