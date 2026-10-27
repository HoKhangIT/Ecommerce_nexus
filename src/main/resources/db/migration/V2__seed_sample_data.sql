-- Seed default ADMIN account (Password is: password123)
INSERT INTO
    users (
        email,
        password_hash,
        full_name,
        role
    )
VALUES (
        'admin@nexus.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a',
        'System Administrator',
        'ROLE_ADMIN'
    )
ON CONFLICT (email) DO NOTHING;

-- Seed default categories
INSERT INTO
    categories (name, slug)
VALUES ('Gaming Gear', 'gaming-gear'),
    ('Laptops', 'laptops')
ON CONFLICT (slug) DO NOTHING;
