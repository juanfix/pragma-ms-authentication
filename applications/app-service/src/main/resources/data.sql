INSERT INTO role (id, name, description) VALUES
 (1, 'ADMIN', 'Administrador del sistema'),
 (2, 'CLIENT', 'Cliente de la plataforma')
 ON CONFLICT (id) DO NOTHING;