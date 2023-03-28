INSERT INTO user_mfa_settings(id, user_id) SELECT users.id, users.id FROM users;

INSERT INTO user_mfa_settings_authorized_mfa_types(user_mfa_settings_id, authorized_mfa_types)  SELECT id, 'NONE'
    FROM user_mfa_settings;


INSERT INTO user_settings(id, user_id, mfa_settings_id) SELECT users.id, users.id, user_mfa_settings.id
FROM users INNER JOIN user_mfa_settings ON users.id = user_mfa_settings.user_id;

ALTER TABLE users ADD COLUMN user_settings_id BIGINT CONSTRAINT fk_user_settings
    REFERENCES user_settings(id);

UPDATE users SET user_settings_id = settings.id FROM user_settings as settings WHERE users.id=settings.user_id;

ALTER TABLE users ALTER COLUMN user_settings_id SET NOT NULL;
