-- Insert Admin user if not exists
INSERT INTO users (name, email, password, role)
SELECT 'Admin', 'admin@email.com', 'Admin@1234', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@email.com'
);

-- Insert DummyUser if not exists
INSERT INTO users (name, email, password, role)
SELECT 'DummyUser', 'user@email.com', 'User@1234', 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'user@email.com'
);
