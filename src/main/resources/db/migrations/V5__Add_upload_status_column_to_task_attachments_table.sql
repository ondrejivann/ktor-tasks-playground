ALTER TABLE task_attachments
    ADD COLUMN upload_status VARCHAR(20) DEFAULT 'PENDING' NOT NULL;

UPDATE task_attachments
SET upload_status = 'UPLOADED';