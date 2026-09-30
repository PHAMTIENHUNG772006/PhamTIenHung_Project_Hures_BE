-- =============================================================================
-- Migration: V12__seed_default_data.sql
-- Description: Khoi tao du lieu mau (Branch, Users, Areas, Tables, Categories, Menu)
-- dam bao he thong hoat dong ngay khi chay tren Docker PostgreSQL
-- =============================================================================

-- 1. Khoi tao Chi nhanh mac dinh
INSERT INTO branches (id, code, name, address, phone, email, status, opening_time, closing_time)
VALUES (
    1,
    'CN-HN-01',
    'Chi Nhánh Hà Nội Trung Tâm',
    'Số 1 Phố Huế, Q. Hoàn Kiếm, TP. Hà Nội',
    '0901234567',
    'hanoi@omniresto.vn',
    'ACTIVE',
    '08:00:00',
    '23:00:00'
)
ON CONFLICT (id) DO UPDATE 
SET code = EXCLUDED.code, 
    name = EXCLUDED.name, 
    status = EXCLUDED.status;

-- Reset sequence cho branches neu can
SELECT setval(pg_get_serial_sequence('branches', 'id'), COALESCE(MAX(id), 1)) FROM branches;

-- 2. Khoi tao 5 tai khoan nguoi dung demo (Mat khau mac dinh: 123456)
-- Hash BCrypt cua '123456' la: $2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi
INSERT INTO users (id, branch_id, username, password, password_hash, full_name, role, status, is_active, phone_number)
VALUES 
    (1, 1, 'admin', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', 'Quản Trị Viên Hệ Thống', 'ADMIN', 'ACTIVE', TRUE, '0900000001'),
    (2, 1, 'manager', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', 'Quản Lý Chi Nhánh', 'MANAGER', 'ACTIVE', TRUE, '0900000002'),
    (3, 1, 'cashier', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', 'Thu Ngân Chính', 'CASHIER', 'ACTIVE', TRUE, '0900000003'),
    (4, 1, 'chef', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', 'Bếp Trưởng', 'CHEF', 'ACTIVE', TRUE, '0900000004'),
    (5, 1, 'waiter', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', '$2a$10$TlD8sAbXq6iL7JSn/tNgW.d.hz5WZYtSYkO3FYWYpIBXx0GABGaNi', 'Nhân Viên Phục Vụ', 'WAITER', 'ACTIVE', TRUE, '0900000005')
ON CONFLICT (username) DO UPDATE 
SET password = EXCLUDED.password,
    password_hash = EXCLUDED.password_hash,
    status = 'ACTIVE',
    is_active = TRUE;

SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1)) FROM users;

-- 3. Khoi tao Khu vuc & Ban an
INSERT INTO areas (id, branch_id, name)
VALUES 
    (1, 1, 'Khu Vực Tầng 1'),
    (2, 1, 'Khu Vực Tầng 2 - Ngoài Trời')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('areas', 'id'), COALESCE(MAX(id), 1)) FROM areas;

INSERT INTO dining_tables (id, area_id, table_number, capacity, status)
VALUES 
    (1, 1, 'Bàn 01', 4, 'AVAILABLE'),
    (2, 1, 'Bàn 02', 4, 'AVAILABLE'),
    (3, 1, 'Bàn 03', 6, 'AVAILABLE'),
    (4, 1, 'Bàn 04', 2, 'AVAILABLE'),
    (5, 2, 'Bàn VIP 01', 8, 'AVAILABLE'),
    (6, 2, 'Bàn VIP 02', 10, 'AVAILABLE')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('dining_tables', 'id'), COALESCE(MAX(id), 1)) FROM dining_tables;

-- 4. Khoi tao Danh muc & Mon an
INSERT INTO categories (id, name, description)
VALUES 
    (1, 'Khai Vị', 'Các món nhẹ mở đầu bữa tiệc'),
    (2, 'Món Chính', 'Món ăn no, đậm đà hương vị truyền thống'),
    (3, 'Đồ Uống', 'Nước ép, cocktail, bia & nước ngọt')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('categories', 'id'), COALESCE(MAX(id), 1)) FROM categories;

INSERT INTO menu_items (id, category_id, name, price, status)
VALUES 
    (1, 1, 'Khoai Tây Chiên Lắc Phô Mai', 45000, 'AVAILABLE'),
    (2, 1, 'Gỏi Cuốn Tôm Thịt Đặc Biệt', 65000, 'AVAILABLE'),
    (3, 2, 'Bò Bít Tết Sốt Tiêu Đen', 185000, 'AVAILABLE'),
    (4, 2, 'Cơm Chiên Hải Sản Hoàng Kim', 95000, 'AVAILABLE'),
    (5, 2, 'Mì Ý Sốt Bò Bằm Bolognaise', 110000, 'AVAILABLE'),
    (6, 3, 'Trà Đào Cam Sả Tươi', 39000, 'AVAILABLE'),
    (7, 3, 'Cà Phê Muối Hải Phòng', 35000, 'AVAILABLE')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('menu_items', 'id'), COALESCE(MAX(id), 1)) FROM menu_items;
