CREATE OR REPLACE FUNCTION prevent_split_non_pending()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.order_id != OLD.order_id AND OLD.status != 'PENDING' THEN
        RAISE EXCEPTION
          'Không thể tách món đang % sang order khác (chỉ tách được PENDING)',
          OLD.status;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_split_non_pending
BEFORE UPDATE OF order_id ON order_items
FOR EACH ROW EXECUTE PROCEDURE prevent_split_non_pending();