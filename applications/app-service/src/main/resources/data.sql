INSERT INTO role (id, name, description) VALUES
 (1, 'ADMIN', 'Administrador del sistema'),
 (2, 'ASESOR', 'Asesor de la plataforma'),
 (2, 'CLIENTE', 'Cliente de la plataforma')
 ON CONFLICT (id) DO NOTHING;