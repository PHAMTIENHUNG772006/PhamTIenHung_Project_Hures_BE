CREATE OR REPLACE FUNCTION update_ingredient_avg_cost()
RETURNS TRIGGER AS $$
DECLARE
    v_old_stock DECIMAL(12,3);
    v_old_avg DECIMAL(12,2);
    v_new_qty DECIMAL(12,3);
    v_import_price DECIMAL(12,2);
BEGIN
    IF (NEW.transaction_type = 'IMPORT' AND NEW.quantity > 0) THEN
        SELECT current_stock, avg_cost_price INTO v_old_stock, v_old_avg
        FROM ingredients WHERE id = NEW.ingredient_id;

        v_new_qty := NEW.quantity;
        v_import_price := NEW.unit_price;

        IF (v_old_stock + v_new_qty) > 0 THEN
            UPDATE ingredients
            SET avg_cost_price = ((v_old_stock * v_old_avg) + (v_new_qty * v_import_price))
                                  / (v_old_stock + v_new_qty),
                current_stock = current_stock + v_new_qty
            WHERE id = NEW.ingredient_id;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_avg_cost
BEFORE INSERT ON stock_transactions
FOR EACH ROW EXECUTE PROCEDURE update_ingredient_avg_cost();

CREATE OR REPLACE FUNCTION process_auto_stock_deduction()
RETURNS TRIGGER AS $$
DECLARE
    r RECORD;
    v_total_required DECIMAL(12,3);
    v_is_negative BOOLEAN := FALSE;
BEGIN
    IF (NEW.status = 'READY' AND (OLD.status IS NULL OR OLD.status != 'READY')) THEN
        FOR r IN
            SELECT recipe.quantity_required,
                   ing.id AS ingredient_id, ing.current_stock
            FROM recipes recipe
            JOIN ingredients ing ON ing.id = recipe.ingredient_id
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
FOR EACH ROW EXECUTE PROCEDURE process_auto_stock_deduction();