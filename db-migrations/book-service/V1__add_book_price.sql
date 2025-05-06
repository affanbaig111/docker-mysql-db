DELIMITER $$

CREATE PROCEDURE add_book_price_if_not_exists()
BEGIN
  IF NOT EXISTS (
    SELECT * FROM information_schema.columns
    WHERE table_schema='book' AND table_name='book' AND column_name='book_price'
  ) THEN
ALTER TABLE book ADD COLUMN book_price DECIMAL(10,2);
END IF;
END$$

DELIMITER ;

CALL add_book_price_if_not_exists();
DROP PROCEDURE add_book_price_if_not_exists;
