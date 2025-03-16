-- Tabulka pro stavy úkolů
CREATE TABLE task_statuses
(
    id            SERIAL PRIMARY KEY,
    code          VARCHAR(20) NOT NULL UNIQUE,
    name          VARCHAR(50) NOT NULL,
    description   TEXT,
    color         VARCHAR(20),
    icon          VARCHAR(50),
    display_order INT         NOT NULL DEFAULT 0
);

-- Naplnění tabulky výchozími hodnotami
INSERT INTO task_statuses (code, name, color, display_order)
VALUES ('pending', 'Čeká na zpracování', '#FFC107', 1),
       ('in_progress', 'V řešení', '#2196F3', 2),
       ('completed', 'Dokončeno', '#4CAF50', 3),
       ('cancelled', 'Zrušeno', '#F44336', 4);