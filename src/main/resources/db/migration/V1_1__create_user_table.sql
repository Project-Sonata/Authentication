CREATE TABLE users(
 id BIGINT NOT NULL PRIMARY KEY,
 email VARCHAR(255) UNIQUE NOT NULL,
 password VARCHAR(3000) NOT NULL,
 is_activated BOOLEAN NOT NULL DEFAULT FALSE
)