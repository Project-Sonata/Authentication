CREATE TABLE confirmation_codes(
    id BIGINT NOT NULL PRIMARY KEY,
    code varchar(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    is_activated BOOLEAN DEFAULT FALSE NOT NULL
)
