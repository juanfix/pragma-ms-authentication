-- Tabla role
CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Tabla users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NULL,
    address VARCHAR(255) NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    identity_number VARCHAR(20) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    base_salary INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_identity_number ON users(identity_number);
CREATE INDEX IF NOT EXISTS idx_user_role_id ON users(role_id);

INSERT INTO role (id, name, description) VALUES
 (1, 'ADMIN', 'Administrador del sistema'),
 (2, 'ASESOR', 'Asesor de la plataforma'),
 (2, 'CLIENTE', 'Cliente de la plataforma')
 ON CONFLICT (id) DO NOTHING;