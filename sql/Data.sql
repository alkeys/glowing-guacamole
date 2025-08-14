-- ================================
-- INSERCIÓN DE DATOS DE EJEMPLO
-- ================================

-- Inserción de usuarios
-- Nota: La contraseña 'password123' está encriptada con un hash ficticio para fines de demostración.
INSERT INTO usuarios (nombre_usuario, contrasena_hash, rol) VALUES
('admin_user', 'hash_admin123', 'administrador'),
('tecnico_juan', 'hash_tecnico_juan', 'tecnico'),
('tecnico_maria', 'hash_tecnico_maria', 'tecnico'),
('cliente_ana', 'hash_cliente_ana', 'cliente'),
('cliente_pedro', 'hash_cliente_pedro', 'cliente');

-- Inserción de tipos de servicio
INSERT INTO tipos_servicio (nombre_tipo) VALUES
('Hardware'),
('Software'),
('Redes'),
('Soporte General');

-- Inserción de estados del ticket
INSERT INTO estados_ticket (nombre_estado) VALUES
('Abierto'),
('En proceso'),
('Cerrado');

-- Inserción de clientes
INSERT INTO clientes (id_usuario, nombre_completo, correo, telefono) VALUES
(4, 'Ana García', 'ana.garcia@email.com', '555-1234'),
(5, 'Pedro López', 'pedro.lopez@email.com', '555-5678');

-- Inserción de técnicos
INSERT INTO tecnicos (id_usuario, nombre_completo, especialidad, activo) VALUES
(2, 'Juan Pérez', 'Hardware', TRUE),
(3, 'María Rodríguez', 'Software', TRUE),
(NULL, 'Carlos Sánchez', 'Redes', TRUE); -- Un técnico sin cuenta de usuario asociada

-- Inserción de tickets
INSERT INTO tickets (id_cliente, id_tipo_servicio, fecha_solicitud, id_tecnico, id_estado, diagnostico) VALUES
(1, 1, '2023-10-25', 1, 1, 'La computadora no enciende.'),
(2, 2, '2023-10-26', 2, 2, 'El programa de contabilidad se cierra inesperadamente.'),
(1, 3, '2023-10-27', 3, 2, 'No hay conexión a internet en mi oficina.'),
(2, 4, '2023-10-28', NULL, 1, 'Necesito ayuda para instalar una impresora.');

-- Actualización de tickets para simular el cierre de uno
UPDATE tickets
SET id_estado = 3, fecha_asignacion = '2023-10-25', fecha_cierre = '2023-10-26', solucion = 'Se reemplazó la fuente de poder defectuosa.'
WHERE id_ticket = 1;

-- Actualización de un ticket en proceso
UPDATE tickets
SET fecha_asignacion = '2023-10-26'
WHERE id_ticket = 2;

UPDATE tickets
SET fecha_asignacion = '2023-10-27'
WHERE id_ticket = 3;