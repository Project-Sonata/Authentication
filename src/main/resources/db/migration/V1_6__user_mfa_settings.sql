CREATE TABLE user_mfa_settings_authorized_mfa_types(
    user_mfa_settings_id BIGINT PRIMARY KEY,
    authorized_mfa_types varchar(255)
);

CREATE TABLE user_mfa_settings(
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id)
)
