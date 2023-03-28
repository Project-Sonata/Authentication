ALTER TABLE users ADD COLUMN natural_id VARCHAR(255) NOT NULL DEFAULT random()::text;
