-- Pouze pro prioritu používáme ENUM
CREATE TYPE task_priority AS ENUM ('low', 'medium', 'high', 'vital');

-- Můžeme přidat užitečné funkce
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ language 'plpgsql';