-- =============================================================================
-- Migration: V1__init_tenant_schema.sql
-- Description: Khởi tạo toàn bộ lược đồ CSDL cho hệ thống Restaurant ERP & POS
-- Engine: PostgreSQL 15+ (UTF8)
-- =============================================================================



-- Tự động cập nhật cột updated_at
CREATE OR REPLACE FUNCTION update_timestamp_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- -----------------------------------------------------------------------------
-- 1. PHÂN HỆ QUẢN TRỊ CHI NHÁNH & NGƯỜI DÙNG (Multi-Tenant Core & RBAC)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS branches (
                                        id BIGSERIAL PRIMARY KEY,
                                        code VARCHAR(50) NOT NULL,
                                        name VARCHAR(255) NOT NULL,
                                        address VARCHAR(255),
                                        phone VARCHAR(50),
                                        email VARCHAR(100),
                                        tax_code VARCHAR(50),
                                        image_url VARCHAR(255),
                                        opening_time TIME,
                                        closing_time TIME,
                                        status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        CONSTRAINT uk_branch_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     branch_id BIGINT,
                                     username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- ADMIN, MANAGER, CASHIER, CHEF, WAITER
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_username UNIQUE (username),
    CONSTRAINT fk_user_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE SET NULL
    );

-- -----------------------------------------------------------------------------
-- 2. PHÂN HỆ KHU VỰC & BÀN ĂN (FOH Table Layout)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS areas (
                                     id BIGSERIAL PRIMARY KEY,
                                     branch_id BIGINT NOT NULL,
                                     name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_area_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS dining_tables (
                                             id BIGSERIAL PRIMARY KEY,
                                             area_id BIGINT NOT NULL,
                                             table_number VARCHAR(50) NOT NULL,
    capacity INT NOT NULL DEFAULT 4,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OCCUPIED, RESERVED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dining_table_area FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE CASCADE
    );

-- -----------------------------------------------------------------------------
-- 3. PHÂN HỆ THỰC ĐƠN & DANH MỤC (Menu Management)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS menu_items (
                                          id BIGSERIAL PRIMARY KEY,
                                          category_id BIGINT NOT NULL,
                                          name VARCHAR(255) NOT NULL,
    price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    image_url VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OUT_OF_STOCK, INACTIVE
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_item_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
    );

-- -----------------------------------------------------------------------------
-- 4. PHÂN HỆ BÁN HÀNG POS & THANH TOÁN (POS & Order Processing)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGSERIAL PRIMARY KEY,
                                      branch_id BIGINT NOT NULL,
                                      table_id BIGINT,
                                      user_id BIGINT NOT NULL,
                                      order_code VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_order_code UNIQUE (order_code),
    CONSTRAINT fk_order_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES dining_tables(id) ON DELETE SET NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGSERIAL PRIMARY KEY,
                                           order_id BIGINT NOT NULL,
                                           menu_item_id BIGINT NOT NULL,
                                           quantity INT NOT NULL DEFAULT 1,
                                           price DECIMAL(12, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ORDERED', -- ORDERED, PREPARING, READY, SERVED, CANCELLED
    note VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        order_id BIGINT NOT NULL,
                                        payment_method VARCHAR(50) NOT NULL, -- CASH, CREDIT_CARD, BANK_TRANSFER, QR_CODE
    amount DECIMAL(12, 2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS', -- SUCCESS, FAILED, REFUNDED
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

-- -----------------------------------------------------------------------------
-- 5. PHÂN HỆ ĐẶT BÀN TRƯỚC (Reservation)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGSERIAL PRIMARY KEY,
                                        branch_id BIGINT NOT NULL,
                                        table_id BIGINT,
                                        customer_name VARCHAR(150) NOT NULL,
    customer_phone VARCHAR(50) NOT NULL,
    guest_count INT NOT NULL DEFAULT 1,
    booking_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, COMPLETED, CANCELLED
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_table FOREIGN KEY (table_id) REFERENCES dining_tables(id) ON DELETE SET NULL
    );

-- -----------------------------------------------------------------------------
-- 6. PHÂN HỆ QUẢN TRỊ KHO & ĐỊNH LƯỢNG MÓN (Inventory & BOM)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingredient_masters (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL, -- kg, gram, liter, piece,...
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ingredient_master_code UNIQUE (code)
    );

CREATE TABLE IF NOT EXISTS ingredients (
                                           id BIGSERIAL PRIMARY KEY,
                                           branch_id BIGINT NOT NULL,
                                           ingredient_master_id BIGINT NOT NULL,
                                           current_stock DECIMAL(12, 3) NOT NULL DEFAULT 0.000,
    min_stock_level DECIMAL(12, 3) NOT NULL DEFAULT 0.000,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_branch_ingredient UNIQUE (branch_id, ingredient_master_id),
    CONSTRAINT fk_ingredient_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient_master FOREIGN KEY (ingredient_master_id) REFERENCES ingredient_masters(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS recipes (
                                       id BIGSERIAL PRIMARY KEY,
                                       menu_item_id BIGINT NOT NULL,
                                       ingredient_master_id BIGINT NOT NULL,
                                       quantity_required DECIMAL(12, 3) NOT NULL,
    CONSTRAINT uk_recipe_item_master UNIQUE (menu_item_id, ingredient_master_id),
    CONSTRAINT fk_recipe_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_recipe_ingredient_master FOREIGN KEY (ingredient_master_id) REFERENCES ingredient_masters(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS stock_transactions (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  branch_id BIGINT NOT NULL,
                                                  ingredient_id BIGINT NOT NULL,
                                                  transaction_type VARCHAR(50) NOT NULL, -- IMPORT, EXPORT, AUTO_DEDUCTION, ADJUSTMENT
    quantity DECIMAL(12, 3) NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_tx_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_tx_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
    );

-- -----------------------------------------------------------------------------
-- 7. PHÂN HỆ QUẢN TRỊ NHÂN SỰ & BÁO CÁO TÀI CHÍNH (HRM & Financial Reporting)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS work_shifts (
                                           id BIGSERIAL PRIMARY KEY,
                                           branch_id BIGINT NOT NULL,
                                           user_id BIGINT NOT NULL,
                                           shift_date DATE NOT NULL,
                                           start_time TIME NOT NULL,
                                           end_time TIME NOT NULL,
                                           check_in_time TIMESTAMP,
                                           check_out_time TIMESTAMP,
                                           status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, CHECKED_IN, COMPLETED, ABSENT
    CONSTRAINT fk_work_shift_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    CONSTRAINT fk_work_shift_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS daily_financial_summaries (
                                                         id BIGSERIAL PRIMARY KEY,
                                                         branch_id BIGINT NOT NULL,
                                                         report_date DATE NOT NULL,
                                                         total_revenue DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    total_cost DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    net_profit DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    order_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_daily_summary_branch_date UNIQUE (branch_id, report_date),
    CONSTRAINT fk_summary_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE
    );

-- -----------------------------------------------------------------------------
-- 8. TRIGGERS TỰ ĐỘNG CẬP NHẬT updated_at CHO CÁC BẢNG
-- -----------------------------------------------------------------------------
CREATE TRIGGER trg_branches_updated_at BEFORE UPDATE ON branches FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_dining_tables_updated_at BEFORE UPDATE ON dining_tables FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_menu_items_updated_at BEFORE UPDATE ON menu_items FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_order_items_updated_at BEFORE UPDATE ON order_items FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_bookings_updated_at BEFORE UPDATE ON bookings FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();
CREATE TRIGGER trg_ingredients_updated_at BEFORE UPDATE ON ingredients FOR EACH ROW EXECUTE FUNCTION update_timestamp_column();

-- -----------------------------------------------------------------------------
-- 9. COMPOSITE INDEXES CHO HIỆU NĂNG TRUY VẤN MULTI-TENANT
-- -----------------------------------------------------------------------------
CREATE INDEX idx_orders_branch_status ON orders (branch_id, status);
CREATE INDEX idx_orders_branch_created ON orders (branch_id, created_at);
CREATE INDEX idx_order_items_order_status ON order_items (order_id, status);
CREATE INDEX idx_bookings_branch_time ON bookings (branch_id, booking_time);
CREATE INDEX idx_stock_tx_branch_created ON stock_transactions (branch_id, created_at);
CREATE INDEX IF NOT EXISTS idx_work_shifts_branch_date ON work_shifts (branch_id, shift_date);