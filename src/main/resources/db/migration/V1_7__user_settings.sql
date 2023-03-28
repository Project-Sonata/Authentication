CREATE TABLE user_settings(
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  user_id BIGINT,
  mfa_settings_id BIGINT,

  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_mfa_settings FOREIGN KEY (mfa_settings_id) REFERENCES user_mfa_settings(id)
)
