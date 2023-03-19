ALTER TABLE confirmation_codes ADD COLUMN user_id BIGINT NOT NULL DEFAULT -1;

ALTER TABLE confirmation_codes ADD CONSTRAINT user_confirmation_code_id FOREIGN KEY (user_id) REFERENCES users;
