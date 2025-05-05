-- Only add 'book_price' column if it doesn't exist
SET @column_exists := (
  SELECT COUNT(*) 
  FROM information_schema.columns 
  WHERE table_schema = 'book' 
    AND table_name = 'book' 
    AND column_name = 'book_price'
);

SET @alter_stmt := IF(@column_exists = 0,
    'ALTER TABLE book ADD COLUMN book_price DECIMAL(10,2);',
    'SELECT "Column already exists";'
);

PREPARE stmt FROM @alter_stmt;
EXECUTE stmt;
