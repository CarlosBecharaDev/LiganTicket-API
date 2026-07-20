-- Datos semilla para LiganTicket
-- Idempotente: no duplica datos si ya existen (por ddl-auto=update)

-- Usuarios de prueba (password: admin123 y user123, BCrypt hashed)
INSERT INTO usuario (nombre, email, password, rol, activo, creado_en)
SELECT 'Administrador', 'admin@liganticket.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@liganticket.com');

INSERT INTO usuario (nombre, email, password, rol, activo, creado_en)
SELECT 'Usuario Demo', 'user@liganticket.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'user@liganticket.com');

-- Tickets de ejemplo
INSERT INTO ticket (titulo, descripcion, estado, usuario_id, creado_en)
SELECT 'Error al iniciar sesion', 'El sistema muestra error 500 cuando intento loguearme con mi cuenta', 'PENDIENTE', u.id, NOW()
FROM usuario u WHERE u.email = 'user@liganticket.com'
AND NOT EXISTS (SELECT 1 FROM ticket WHERE titulo = 'Error al iniciar sesion');

INSERT INTO ticket (titulo, descripcion, estado, usuario_id, creado_en)
SELECT 'Agregar filtro de busqueda', 'Se necesita un filtro por estado en la lista de tickets', 'EN_PROCESO', u.id, NOW()
FROM usuario u WHERE u.email = 'user@liganticket.com'
AND NOT EXISTS (SELECT 1 FROM ticket WHERE titulo = 'Agregar filtro de busqueda');

INSERT INTO ticket (titulo, descripcion, estado, usuario_id, creado_en)
SELECT 'Actualizar documentacion', 'Documentar el endpoint de login con los nuevos parametros', 'COMPLETADO', u.id, NOW()
FROM usuario u WHERE u.email = 'admin@liganticket.com'
AND NOT EXISTS (SELECT 1 FROM ticket WHERE titulo = 'Actualizar documentacion');
