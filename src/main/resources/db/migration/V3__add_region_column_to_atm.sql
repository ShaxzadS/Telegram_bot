ALTER TABLE atm ADD COLUMN region varchar(255) NOT NULL DEFAULT 'Центр';
UPDATE atm SET region = 'Центр' WHERE region IS NULL;
