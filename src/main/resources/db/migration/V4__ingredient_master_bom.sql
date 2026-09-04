-- =============================================================================
-- Migration: V4__ingredient_master_bom.sql
-- Description: Cập nhật cấu trúc nguyên liệu, công thức (BOM) và trừ kho tự động v2
-- =============================================================================

CREATE TABLE IF NOT EXISTS ingredient_master (
                                                 id SERIAL PRIMARY KEY,
                                                 name VARCHAR(150) NOT NULL UNIQUE,
    unit VARCHAR(20) NOT NULL
    );

ALTER TABLE recipes
DROP CONSTRAINT IF EXISTS uk_recipe_item_ingredient,
    DROP CONSTRAINT IF EXISTS uk_recipe_item_master,
    DROP COLUMN IF EXISTS ingredient_id,
    ADD COLUMN IF NOT EXISTS ingredient_master_id INT,
    ADD CONSTRAINT fk_recipes_ingredient_master_id FOREIGN KEY (ingredient_master_id) REFERENCES ingredient_master(id) ON DELETE RESTRICT,
    ADD CONSTRAINT uk_recipe_item_ingredient_master UNIQUE (menu_item_id, ingredient_master_id);

ALTER TABLE ingredients
DROP CONSTRAINT IF EXISTS uk_branch_ingredient_name,
    DROP CONSTRAINT IF EXISTS uk_branch_ingredient,
    ADD COLUMN IF NOT EXISTS master_id INT,
    ADD CONSTRAINT fk_ingredients_master_id FOREIGN KEY (master_id) REFERENCES ingredient_master(id) ON DELETE RESTRICT,
    ADD CONSTRAINT uk_branch_ingredient_master UNIQUE (branch_id, master_id);

ALTER TABLE stock_transactions
    ADD COLUMN IF NOT EXISTS branch_id INT,
    ADD CONSTRAINT fk_stock_transactions_branch_id FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE RESTRICT;

CREATE OR REPLACE FUNCTION sync_stock_tx_branch()
RETURNS TRIGGER AS $$
BEGIN
SELECT branch_id INTO NEW.branch_id FROM ingredients WHERE id = NEW.ingredient_id;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sync_stock_tx_branch ON stock_transactions;
CREATE TRIGGER trg_sync_stock_tx_branch
    BEFORE INSERT ON stock_transactions
    FOR EACH ROW EXECUTE PROCEDURE sync_stock_tx_branch();

DROP TRIGGER IF EXISTS trg_deduct_stock_on_item_ready ON order_items;
DROP FUNCTION IF EXISTS process_auto_stock_deduction();

CREATE OR REPLACE FUNCTION process_auto_stock_deduction_v2()
RETURNS TRIGGER AS $$
DECLARE
r RECORD;
    v_total_required DECIMAL(12,3);
    v_is_negative BOOLEAN := FALSE;
BEGIN
    IF (NEW.status = 'READY' AND (OLD.status IS NULL OR OLD.status != 'READY')) THEN
        FOR r IN
SELECT im.id AS master_id, recipe.quantity_required,
       ing.id AS ingredient_id, ing.current_stock
FROM recipes recipe
         JOIN ingredient_master im ON recipe.ingredient_master_id = im.id
         JOIN order_items oi2 ON oi2.id = NEW.id
         JOIN orders o ON o.id = oi2.order_id
         JOIN ingredients ing ON ing.master_id = im.id AND ing.branch_id = o.branch_id
WHERE recipe.menu_item_id = NEW.menu_item_id
    LOOP
            v_total_required := r.quantity_required * NEW.quantity;

IF (r.current_stock < v_total_required) THEN
                v_is_negative := TRUE;
END IF;

UPDATE ingredients
SET current_stock = current_stock - v_total_required
WHERE id = r.ingredient_id;

INSERT INTO stock_transactions (
    ingredient_id, transaction_type, quantity, reference_order_id, note
) VALUES (
             r.ingredient_id,
             'EXPORT_ORDER',
             -v_total_required,
             NEW.order_id,
             CASE
                 WHEN v_is_negative THEN
                     FORMAT('[CẢNH BÁO ÂM KHO] Trừ kho tự động cho Order Item #%s (Tồn cũ: %s, Cần: %s)',
                            NEW.id, r.current_stock, v_total_required)
                 ELSE FORMAT('Trừ kho tự động cho Order Item #%s', NEW.id)
                 END
         );
END LOOP;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_deduct_stock_on_item_ready
    AFTER UPDATE OF status ON order_items
    FOR EACH ROW EXECUTE PROCEDURE process_auto_stock_deduction_v2();