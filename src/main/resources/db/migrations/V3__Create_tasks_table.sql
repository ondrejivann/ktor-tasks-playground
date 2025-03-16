CREATE TABLE tasks (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(128) NOT NULL,
                       description VARCHAR(1024) NOT NULL,
                       status_id INT NOT NULL REFERENCES task_statuses(id),
                       priority task_priority NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT tasks_name_unique UNIQUE (name)
);

-- Trigger pro automatickou aktualizaci updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_task_statuses_updated_at
    BEFORE UPDATE ON task_statuses
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();