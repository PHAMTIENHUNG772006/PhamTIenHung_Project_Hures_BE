-- =============================================================================
-- Migration: V11__sync_users_schema.sql
-- Description: Dong bo schema bang users, them cot is_active, phone_number, pin_code, password_hash
-- va cap nhat mat khau mac dinh '123456' cho cac tai khoan demo
-- =============================================================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS phone_number VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS pin_code VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE';

-- Dong bo is_active theo status neu status co gia tri
UPDATE users 
SET is_active = (CASE WHEN status = 'INACTIVE' THEN FALSE ELSE TRUE END)
WHERE is_active IS NULL;

-- Cap nhat hash BCrypt hop le cua mat khau '123456' cho tat ca tai khoan co san
UPDATE users 
SET password = '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi',
    password_hash = '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi',
    status = 'ACTIVE',
    is_active = TRUE
WHERE id IN (1, 2, 3, 4, 5);

-- Cap nhat password_hash cho bat ky user nao chua co password_hash
UPDATE users 
SET password_hash = password 
WHERE password_hash IS NULL AND password IS NOT NULL;
