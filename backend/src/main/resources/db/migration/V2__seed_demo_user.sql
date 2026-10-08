INSERT INTO users (
    email,
    password_hash,
    display_name,
    account_status,
    created_at
)
VALUES (
    'demo@payops.local',
    '$2a$10$o7Ek1lF6KSF2g4/3Z3h1C.2dHZ1eG0T8JBei.alZwln6LYK1xk8Gm',
    'Demo Merchant',
    'ACTIVE',
    CURRENT_TIMESTAMP
);