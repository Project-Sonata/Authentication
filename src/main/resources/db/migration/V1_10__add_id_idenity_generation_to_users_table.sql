ALTER TABLE users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY;

-- Set the last ID as starting point
SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(MAX(id), 1)) FROM users;